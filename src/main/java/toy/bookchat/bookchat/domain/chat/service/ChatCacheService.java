package toy.bookchat.bookchat.domain.chat.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import toy.bookchat.bookchat.domain.chat.service.cache.ChatRoomCache;
import toy.bookchat.bookchat.domain.chat.service.cache.ParticipantCache;
import toy.bookchat.bookchat.domain.chat.service.cache.UserCache;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
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
    public UserCache findUserByUserId(Long userId) {
        return UserCache.of(
            userRepository.findById(userId).orElseThrow(UserNotFoundException::new));
    }

    @Cacheable(cacheNames = "chatroom")
    public ChatRoomCache findChatRoomByRoomSid(String roomSid) {
        return ChatRoomCache.of(chatRoomRepository.findByRoomSid(roomSid).orElseThrow(
            ChatRoomNotFoundException::new));
    }

    @Cacheable(cacheNames = "participant", key = "'U' + #userId + 'CR' + #chatRoomId")
    public ParticipantCache findParticipantByUserIdAndChatRoomId(Long userId, Long chatRoomId) {
        return ParticipantCache.of(
            participantRepository.findByUserIdAndChatRoomId(userId, chatRoomId)
                .orElseThrow(NotParticipatedException::new));
    }

    @CacheEvict(cacheNames = "participant", key = "'U' + #userId + 'CR' + #chatRoomId")
    public void deleteParticipantCache(Long userId, Long chatRoomId) {
    }
}
