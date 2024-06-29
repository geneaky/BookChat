package toy.bookchat.bookchat.db_module.device.repository.query;

import static toy.bookchat.bookchat.db_module.device.QDeviceEntity.deviceEntity;
import static toy.bookchat.bookchat.db_module.participant.QParticipantEntity.participantEntity;
import static toy.bookchat.bookchat.db_module.user.QUserEntity.userEntity;
import static toy.bookchat.bookchat.security.token.jwt.QRefreshTokenEntity.refreshTokenEntity;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;
import toy.bookchat.bookchat.db_module.device.DeviceEntity;

@Repository
public class DeviceQueryRepositoryImpl implements DeviceQueryRepository {

    private final JPAQueryFactory queryFactory;

    public DeviceQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public void deleteByUserId(Long userId) {
        queryFactory.delete(deviceEntity)
            .where(deviceEntity.userEntity.id.eq(userId))
            .execute();
    }

    @Override
    public List<DeviceEntity> getDisconnectedUserDevice(Long roomId) {
        return queryFactory.select(deviceEntity)
            .from(deviceEntity)
            .innerJoin(participantEntity).on(deviceEntity.userEntity.eq(participantEntity.userEntity).and(participantEntity.chatRoomEntity.id.eq(roomId)))
            .where(participantEntity.isConnected.isFalse())
            .fetch();
    }

    @Override
    public void deleteExpiredFcmToken() {
        List<Long> deviceIds = queryFactory.select(deviceEntity.id)
            .from(deviceEntity)
            .innerJoin(userEntity).on(deviceEntity.userEntity.id.eq(userEntity.id))
            .innerJoin(refreshTokenEntity).on(refreshTokenEntity.userId.eq(userEntity.id))
            .where(refreshTokenEntity.updatedAt.before(LocalDateTime.now().minusWeeks(6)))
            .fetch();

        queryFactory.delete(deviceEntity)
            .where(deviceEntity.id.in(deviceIds))
            .execute();
    }
}
