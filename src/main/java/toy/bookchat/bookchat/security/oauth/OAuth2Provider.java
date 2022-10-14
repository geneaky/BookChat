package toy.bookchat.bookchat.security.oauth;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OAuth2Provider {
    GOOGLE("google"), KAKAO("kakao");

    private String provider;

    OAuth2Provider(String provider) {
        this.provider = provider;
    }

    @JsonCreator
    public static OAuth2Provider from(String provider) {
        for (OAuth2Provider oauth2Provider : OAuth2Provider.values()) {
            if (oauth2Provider.getValue().equals(provider)) {
                return oauth2Provider;
            }
        }
        return null;
    }

    @JsonValue
    public String getValue() {
        return this.provider;
    }
}
