package toy.bookchat.bookchat.security.oauth;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OAuth2Provider {
    GOOGLE("google"), KAKAO("kakao");

    private String provider;

    public String getProvider() {
        return provider;
    }

    OAuth2Provider(String provider) {
        this.provider = provider;
    }

    public static OAuth2Provider from(String provider) {
        for(OAuth2Provider oAuth2Provider : OAuth2Provider.values()) {
            if(oAuth2Provider.getProvider().equals(provider)) {
                return oAuth2Provider;
            }
        }
        return null;
    }
}
