package toy.bookchat.bookchat.domain.chatroom.repository.query;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import toy.bookchat.bookchat.domain.chatroom.repository.query.dto.response.ChatRoomResponse;

@Repository
public class ChatRoomQueryRepositoryImpl implements ChatRoomQueryRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ChatRoomQueryRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<ChatRoomResponse> test(Pageable pageable, Optional<Long> postCursorId,
        Long userId) {
        String sql = "select * from chat_room "
            + "left outer join participant p1 on chat_room.id = p1.chat_room_id and p1.user_id = :userId "
            + "left outer join "
            + "(select participant.chat_room_id, count(participant.user_id) as room_member_count "
            + "from participant where participant.chat_room_id in (select participant.chat_room_id from participant where participant.user_id = :userId) "
            + "group by participant.chat_room_id) p2 on chat_room.id = p2.chat_room_id "
            + "left outer join "
            + "(select max(chat.id) as chat_id, chat.message as message, chat.chat_room_id as chat_room_id, chat.created_at as last_chat_time "
            + "from chat where chat.user_id = :userId group by chat.chat_room_id) last_chat on last_chat.chat_room_id = chat_room.id "
            + setSizeAndCursorId(pageable, postCursorId);

        MapSqlParameterSource namedParameters = new MapSqlParameterSource().addValue("userId",
            userId);
        return jdbcTemplate.query(sql, namedParameters, (rs, rowNum) ->
            ChatRoomResponse.builder()
                .roomId(rs.getLong("ID"))
                .roomSid(rs.getString("ROOM_SID"))
                .roomName(rs.getString("ROOM_NAME"))
                .roomMemberCount(rs.getInt("ROOM_MEMBER_COUNT"))
                .defaultRoomImageType(rs.getInt("DEFAULT_ROOM_IMAGE_TYPE"))
                .roomImageUri(rs.getString("ROOM_IMAGE_URI"))
                .lastChatContent(rs.getString("MESSAGE"))
                .lastActiveTime(rs.getTimestamp("LAST_CHAT_TIME").toLocalDateTime())
                .build());


    }

    private String setSizeAndCursorId(Pageable pageable, Optional<Long> postCursorId) {
        StringBuilder stringBuilder = new StringBuilder();
        postCursorId.ifPresent(cursorId -> {
            stringBuilder.append("where ");
            stringBuilder.append("chat_room.id > ");
            stringBuilder.append(cursorId);
        });
        stringBuilder.append(" order by last_chat.chat_id desc, chat_room.id desc limit ");
        stringBuilder.append(pageable.getPageSize());
        return stringBuilder.toString();
    }
}
