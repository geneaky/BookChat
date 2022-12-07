package toy.bookchat.bookchat.domain.chatroom.repository.query;

import static toy.bookchat.bookchat.domain.chatroom.QChatRoom.chatRoom;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.extractOrderSpecifierFrom;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.numberBasedPagination;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.toSlice;
import static toy.bookchat.bookchat.domain.participant.QParticipant.participant;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;

@Repository
public class ChatRoomQueryRepositoryImpl implements ChatRoomQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ChatRoomQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Slice<ChatRoom> findUserChatRoomsWithLastChat(Optional<Long> postCursorId,
        Pageable pageable, Long userId) {
        List<ChatRoom> content = queryFactory.select(chatRoom)
            .from(chatRoom)
            .join(participant.chatRoom, chatRoom).on(participant.user.id.eq(userId))
            .where(numberBasedPagination(chatRoom, chatRoom.id, postCursorId, pageable))
            .limit(pageable.getPageSize())
            .orderBy(extractOrderSpecifierFrom(chatRoom, pageable))
            .fetch();

        return toSlice(content, pageable);
    }
}
