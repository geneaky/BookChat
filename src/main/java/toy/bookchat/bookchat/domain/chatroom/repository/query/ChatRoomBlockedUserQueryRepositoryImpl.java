package toy.bookchat.bookchat.domain.chatroom.repository.query;

import static toy.bookchat.bookchat.domain.chatroom.QChatRoomBlockedUser.chatRoomBlockedUser;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoomBlockedUser;

@Repository
public class ChatRoomBlockedUserQueryRepositoryImpl implements ChatRoomBlockedUserQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ChatRoomBlockedUserQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Optional<ChatRoomBlockedUser> findByUserIdAndChatRoomId(Long userId, Long chatRoomId) {
        return Optional.ofNullable(
            queryFactory.select(chatRoomBlockedUser)
                .from(chatRoomBlockedUser)
                .where(chatRoomBlockedUser.user.id.eq(userId)
                    .and(chatRoomBlockedUser.chatRoom.id.eq(chatRoomId)))
                .fetchOne()
        );
    }
}
