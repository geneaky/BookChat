package toy.bookchat.bookchat.domain.chat.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import toy.bookchat.bookchat.domain.chatroom.repository.ChatRoomRepository;
import toy.bookchat.bookchat.domain.participant.repository.ParticipantRepository;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;

@SpringBootTest
@Slf4j
class ChatCacheServiceTest {

    @Autowired
    private ChatCacheService cacheService;
    @Autowired
    private CacheManager cacheManager;

    @MockBean
    UserRepository userRepository;
    @MockBean
    ChatRoomRepository chatRoomRepository;
    @MockBean
    ParticipantRepository participantRepository;

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
        when(user.getNickname()).thenReturn("hihi test success");
        cacheService.findUserByUserId(1L);
        cacheService.findUserByUserId(1L);
        User user1 = cacheManager.getCache("user").get(1L, User.class);
        System.out.println(user1.getNickname());
        cacheManager.getCacheNames()
            .stream()
            .map(cacheName -> ((CaffeineCache) cacheManager.getCache(cacheName)).getNativeCache())
            .forEach(cache -> cache.asMap().keySet().forEach(key -> {
                log.info("key: {} - value: {}", key, cache.getIfPresent(key).toString());
            }));
        System.out.println("==================test");
        verify(userRepository).findById(any());
    }
}