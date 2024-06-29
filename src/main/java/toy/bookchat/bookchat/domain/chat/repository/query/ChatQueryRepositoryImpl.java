package toy.bookchat.bookchat.domain.chat.repository.query;

import static toy.bookchat.bookchat.domain.chat.QChatEntity.chatEntity;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.extractOrderSpecifierFrom;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.numberBasedPagination;
import static toy.bookchat.bookchat.domain.common.RepositorySupport.toSlice;
import static toy.bookchat.bookchat.domain.participant.QParticipantEntity.participantEntity;
import static toy.bookchat.bookchat.domain.user.QUserEntity.userEntity;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import toy.bookchat.bookchat.domain.chat.ChatEntity;

@Repository
public class ChatQueryRepositoryImpl implements ChatQueryRepository {

    private final JPAQueryFactory queryFactory;

    public ChatQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Slice<ChatEntity> getChatRoomChats(Long roomId, Long postCursorId, Pageable pageable,
        Long userId) {
        return toSlice(queryFactory.select(chatEntity)
            .from(chatEntity)
            .where(chatEntity.chatRoomEntity.id.eq(JPAExpressions.select(participantEntity.chatRoomEntity.id)
                    .from(participantEntity)
                    .where(participantEntity.userEntity.id.eq(userId)
                        .and(participantEntity.chatRoomEntity.id.eq(roomId)))),
                numberBasedPagination(chatEntity, chatEntity.id, postCursorId, pageable)
            )
            .limit(pageable.getPageSize())
            .orderBy(extractOrderSpecifierFrom(chatEntity, pageable)).fetch(), pageable);
    }

    @Override
    public Optional<ChatEntity> getUserChatRoomChat(Long chatId, Long userId) {
        return Optional.ofNullable(queryFactory.select(chatEntity)
            .from(chatEntity)
            .join(chatEntity.userEntity, userEntity).fetchJoin()
            .where(chatEntity.id.eq(chatId)
                .and(chatEntity.chatRoomEntity.id.in(
                    JPAExpressions.select(participantEntity.chatRoomEntity.id)
                        .from(participantEntity)
                        .where(participantEntity.userEntity.id.eq(userId)))
                ))
            .fetchOne());
    }
}
