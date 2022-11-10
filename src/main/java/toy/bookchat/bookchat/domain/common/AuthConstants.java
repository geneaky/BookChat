package toy.bookchat.bookchat.domain.common;

public final class AuthConstants {

    public static final String BEARER = "Bearer";
    public static final String AUTHORIZATION = "Authorization";
    public static final String OIDC = "OIDC";
    public static final int BEGIN_INDEX = 7;

    private AuthConstants() {
        throw new IllegalStateException("Can't Make This Class Instance");
    }
}
