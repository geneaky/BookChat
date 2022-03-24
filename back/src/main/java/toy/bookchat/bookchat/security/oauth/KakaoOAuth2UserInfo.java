package toy.bookchat.bookchat.security.oauth;

import java.util.LinkedHashMap;
import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

    public static final String SUB = "sub";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String PICTURE = "picture";

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return (String)attributes.get(SUB);
    }

    @Override
    public String getName() {
        return (String) attributes.get(NAME);
    }

    @Override
    public String getEmail() {
        return (String)((LinkedHashMap<String, Object>)attributes.get("kakao_account")).get(EMAIL);
    }

    @Override
    public String getImageUrl() {
        return null;
    }
}
