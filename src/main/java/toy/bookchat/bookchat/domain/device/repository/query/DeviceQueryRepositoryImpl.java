package toy.bookchat.bookchat.domain.device.repository.query;

import static toy.bookchat.bookchat.domain.device.QDevice.device;
import static toy.bookchat.bookchat.domain.participant.QParticipant.participant;

import com.querydsl.jpa.impl.JPAQueryFactory;
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
}
