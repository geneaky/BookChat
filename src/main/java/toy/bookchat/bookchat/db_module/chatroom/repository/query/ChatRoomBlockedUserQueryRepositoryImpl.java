package toy.bookchat.bookchat.db_module.chatroom.repository.query;

import static toy.bookchat.bookchat.db_module.chatroom.QChatRoomBlockedUserEntity.chatRoomBlockedUserEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import toy.bookchat.bookchat.db_module.chatroom.ChatRoomBlockedUserEntity;

@Repository
public class ChatRoomBlockedUserQueryRepositoryImpl implements ChatRoomBlockedUserQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ChatRoomBlockedUserQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Optional<ChatRoomBlockedUserEntity> findByUserIdAndChatRoomId(Long userId, Long chatRoomId) {
        return Optional.ofNullable(
            queryFactory.select(chatRoomBlockedUserEntity)
                .from(chatRoomBlockedUserEntity)
                .where(chatRoomBlockedUserEntity.userId.eq(userId)
                    .and(chatRoomBlockedUserEntity.chatRoomId.eq(chatRoomId)))
                .fetchOne()
        );
    }
}
