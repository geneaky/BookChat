package toy.bookchat.bookchat.config.cache;

import lombok.Getter;

@Getter
public enum CacheType {

    KAKAO_PUBLIC_KEY("kakao", 259_200, 1),
    GOOGLE_PUBLIC_KEY("google", 259_200, 1);

    private final String cacheName;
    private final int expiredAfterWrite;
    private final int maximumSize;

    CacheType(String cacheName, int expiredAfterWrite, int maximumSize) {
        this.cacheName = cacheName;
        this.expiredAfterWrite = expiredAfterWrite;
        this.maximumSize = maximumSize;
    }
}
