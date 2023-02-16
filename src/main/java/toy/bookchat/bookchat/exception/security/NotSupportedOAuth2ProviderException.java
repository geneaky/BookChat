package toy.bookchat.bookchat.exception.security;

public class NotSupportedOAuth2ProviderException extends RuntimeException {

    public NotSupportedOAuth2ProviderException() {
        super("Not Supported OAuth2 Provider");
    }
}
