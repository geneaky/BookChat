package toy.bookchat.bookchat.security.oauth;


public enum OAuth2Provider {
    GOOGLE("google"), KAKAO("kakao");

    private String provider;

    OAuth2Provider(String provider) {
        this.provider = provider;
    }

    public static OAuth2Provider from(String provider) {
        for (OAuth2Provider oAuth2Provider : OAuth2Provider.values()) {
            if (oAuth2Provider.name().equals(provider)) {
                return oAuth2Provider;
            }
        }
        return null;
    }

    public String getValue() {
        return provider;
    }
}
