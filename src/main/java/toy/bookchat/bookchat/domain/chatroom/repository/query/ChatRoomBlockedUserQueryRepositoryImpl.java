package toy.bookchat.bookchat.domain.chatroom.repository.query;


import static toy.bookchat.bookchat.domain.chatroom.QChatRoomBlockedUserEntity.chatRoomBlockedUserEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import toy.bookchat.bookchat.domain.chatroom.ChatRoomBlockedUserEntity;

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
                .where(chatRoomBlockedUserEntity.userEntity.id.eq(userId)
                    .and(chatRoomBlockedUserEntity.chatRoomEntity.id.eq(chatRoomId)))
                .fetchOne()
        );
    }
}
