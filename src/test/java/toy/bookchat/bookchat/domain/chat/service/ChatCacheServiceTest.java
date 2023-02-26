package toy.bookchat.bookchat.domain.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import toy.bookchat.bookchat.domain.chatroom.ChatRoom;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.domain.participant.Participant;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;

@Slf4j
@SpringBootTest
class ChatCacheServiceTest {

    @MockBean
    UserRepository userRepository;
    @MockBean
    ChatRoomRepository chatRoomRepository;
    @MockBean
    ParticipantRepository participantRepository;
    @Autowired
    private ChatCacheService cacheService;
    @Autowired
    private CacheManager cacheManager;

    @AfterEach
    public void tearDown() {
        Collection<String> cacheNames = cacheManager.getCacheNames();
        for (String cacheName : cacheNames) {
            Cache cache = cacheManager.getCache(cacheName);
            cache.clear();
        }
    }

    @Test
    void 사용자_캐시되어있지_않으면_조회동작_성공() throws Exception {
        User user = mock(User.class);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        cacheService.findUserByUserId(1L);

        verify(userRepository).findById(any());
    }

    @Test
    void 사용자_캐시되어있으면_조회없이_반환_성공() throws Exception {
        User user = mock(User.class);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        cacheService.findUserByUserId(1L);
        cacheService.findUserByUserId(1L);
        verify(userRepository).findById(any());
    }

    @Test
    void 채팅방_캐시되어있지_않으면_조회동작_성공() throws Exception {
        ChatRoom chatRoom = mock(ChatRoom.class);
        when(chatRoomRepository.findByRoomSid(any())).thenReturn(Optional.of(chatRoom));

        cacheService.findChatRoomByRoomSid("roomSid");

        verify(chatRoomRepository).findByRoomSid(any());
    }

    @Test
    void 채팅방_캐시되어있으면_조회없이_반환_성공() throws Exception {
        ChatRoom chatRoom = mock(ChatRoom.class);
        when(chatRoomRepository.findByRoomSid(any())).thenReturn(Optional.of(chatRoom));

        cacheService.findChatRoomByRoomSid("roomSid");
        cacheService.findChatRoomByRoomSid("roomSid");

        verify(chatRoomRepository).findByRoomSid(any());
    }

    @Test
    void 참가자_캐시되어있지_않으면_조회동작_성공() throws Exception {
        Participant participant = mock(Participant.class);

        when(participantRepository.findByUserIdAndChatRoomId(any(), any())).thenReturn(
            Optional.of(participant));

        cacheService.findParticipantByUserIdAndChatRoomId(1L, 1L);

        verify(participantRepository).findByUserIdAndChatRoomId(any(), any());
    }

    @Test
    void 참가자_캐시되어있으면_조회없이_반환_성공() throws Exception {
        Participant participant = mock(Participant.class);

        when(participantRepository.findByUserIdAndChatRoomId(any(), any())).thenReturn(
            Optional.of(participant));

        cacheService.findParticipantByUserIdAndChatRoomId(1L, 1L);
        cacheService.findParticipantByUserIdAndChatRoomId(1L, 1L);

        verify(participantRepository).findByUserIdAndChatRoomId(any(), any());
    }

    @Test
    void 참가자_캐시_삭제_성공() throws Exception {
        Participant participant = mock(Participant.class);

        when(participantRepository.findByUserIdAndChatRoomId(anyLong(), anyLong())).thenReturn(
            Optional.of(participant));

        cacheService.findParticipantByUserIdAndChatRoomId(1L, 1l);
        cacheService.deleteParticipantCache(1L, 1L);

        ConcurrentMap<Object, Object> caches = ((CaffeineCache) cacheManager.getCache(
            "participant")).getNativeCache().asMap();

        assertThat(caches).isEmpty();
    }
}