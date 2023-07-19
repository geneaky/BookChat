package toy.bookchat.bookchat.domain.device.repository.query;

import static toy.bookchat.bookchat.domain.device.QDevice.device;
import static toy.bookchat.bookchat.domain.participant.QParticipant.participant;
import static toy.bookchat.bookchat.domain.user.QUser.user;
import static toy.bookchat.bookchat.security.token.jwt.QRefreshToken.refreshToken1;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;
import toy.bookchat.bookchat.domain.device.Device;

@Repository
public class DeviceQueryRepositoryImpl implements DeviceQueryRepository {

    private final JPAQueryFactory queryFactory;

    public DeviceQueryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public void deleteByUserId(Long userId) {
        queryFactory.delete(device)
            .where(device.user.id.eq(userId))
            .execute();
    }

    @Override
    public List<Device> getDisconnectedUserDevice(Long roomId) {
        return queryFactory.select(device)
            .from(device)
            .innerJoin(participant)
            .on(device.user.eq(participant.user).and(participant.chatRoom.id.eq(roomId)))
            .where(participant.isConnected.isFalse())
            .fetch();
    }

    @Override
    public void deleteExpiredFcmToken() {
        List<Long> deviceIds = queryFactory.select(device.id)
            .from(device)
            .innerJoin(user).on(device.user.id.eq(user.id))
            .innerJoin(refreshToken1).on(refreshToken1.userId.eq(user.id))
            .where(refreshToken1.updatedAt.before(LocalDateTime.now().minusWeeks(6)))
            .fetch();

        queryFactory.delete(device)
            .where(device.id.in(deviceIds))
            .execute();
    }
}
