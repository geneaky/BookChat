package toy.bookchat.bookchat.security.oauth;

import java.util.Map;

public class GoogleOAuth2UserInfo extends OAuth2UserInfo {

    public static final String SUB = "sub";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String PICTURE = "picture";

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String)attributes.get(SUB);
    }

    @Override
    public String getName() {
        return (String)attributes.get(NAME);
    }

    @Override
    public String getEmail() {
        return (String)attributes.get(EMAIL);
    }

    @Override
    public String getImageUrl() {
        return (String)attributes.get(PICTURE);
    }
}
