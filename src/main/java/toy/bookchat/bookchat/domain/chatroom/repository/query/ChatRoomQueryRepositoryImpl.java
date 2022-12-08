package toy.bookchat.bookchat.domain.chatroom.repository.query;

import java.util.List;
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
    public List<ChatRoomResponse> test(Long userId) {
        String sql = "select * from chat_room "
            + "left outer join participant on chat_room.id = participant.chat_room_id and participant.user_id = :userId "
            + "left outer join "
            + "(select max(chat.id) as chat_id, chat.message as message, chat.chat_room_id as chat_room_id, chat.created_at as last_chat_time "
            + "from chat where chat.user_id = :userId group by chat.chat_room_id) last_chat on last_chat.chat_room_id = chat_room.id "
            + "order by last_chat.chat_id desc";

        MapSqlParameterSource namedParameters = new MapSqlParameterSource().addValue("userId",
            userId);
        return jdbcTemplate.query(sql, namedParameters, (rs, rowNum) ->
            ChatRoomResponse.builder()
                .roomId(rs.getLong("ID"))
                .roomSid(rs.getString("ROOM_SID"))
                .roomName(rs.getString("ROOM_NAME"))
                .defaultRoomImageType(rs.getInt("DEFAULT_ROOM_IMAGE_TYPE"))
                .roomImageUri(rs.getString("ROOM_IMAGE_URI"))
                .lastChatContent(rs.getString("MESSAGE"))
                .lastActiveTime(rs.getTimestamp("LAST_CHAT_TIME").toLocalDateTime())
                .build());
    }
}
