package toy.bookchat.bookchat.security.token.openid.google;

public interface GoogleIdTokenFetcher {

    GoogleIdTokenWrapper fetchGoogleIdToken(String token);
}
