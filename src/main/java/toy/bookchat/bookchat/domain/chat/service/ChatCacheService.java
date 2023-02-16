package toy.bookchat.bookchat.domain.chat.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.exception.chatroom.ChatRoomNotFoundException;
import toy.bookchat.bookchat.exception.participant.NotParticipatedException;
import toy.bookchat.bookchat.exception.user.UserNotFoundException;

@Service
public class ChatCacheService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ParticipantRepository participantRepository;

    public ChatCacheService(UserRepository userRepository, ChatRoomRepository chatRoomRepository,
        ParticipantRepository participantRepository) {
        this.userRepository = userRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.participantRepository = participantRepository;
    }

    /* TODO: 2023-02-16 entity말고 entity id를 캐시하는 방식으로 변경
        entity 직접 캐시는 hibernate 2차 캐시를 지원하는 ehcache, infinispan만 가능
        복잡성에 비해 조회 쿼리만 활용한 캐시는 필요없음
     */

    @Cacheable(cacheNames = "user")
    public User findUserByUserId(Long userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    @Cacheable(cacheNames = "chatroom")
    public ChatRoom findChatRoomByRoomSid(String roomSid) {
        return chatRoomRepository.findByRoomSid(roomSid).orElseThrow(
            ChatRoomNotFoundException::new);
    }

    @CachePut(cacheNames = "participant", key = "'U' + #user.id + 'CR' + #chatRoom.id")
    public Participant saveParticipantCache(User user, ChatRoom chatRoom, Participant participant) {
        return participant;
    }

    @Cacheable(cacheNames = "participant", key = "'U' + #user.id + 'CR' + #chatRoom.id")
    public Participant findParticipantByUserAndChatRoom(User user, ChatRoom chatRoom) {
        return participantRepository.findByUserAndChatRoom(user, chatRoom).orElseThrow(
            NotParticipatedException::new);
    }

    @CacheEvict(cacheNames = "participant", key = "'U' + #user.id + 'CR' + #chatRoom.id")
    public void deleteParticipantCache(User user, ChatRoom chatRoom) {
    }
}
