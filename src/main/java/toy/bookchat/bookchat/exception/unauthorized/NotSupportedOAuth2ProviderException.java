package toy.bookchat.bookchat.exception.unauthorized;

import static toy.bookchat.bookchat.exception.ErrorCode.NOT_SUPPORTED_OAUTH2_PROVIDER;

public class NotSupportedOAuth2ProviderException extends UnauthorizedException {

    public NotSupportedOAuth2ProviderException() {
        super(NOT_SUPPORTED_OAUTH2_PROVIDER, "지원하지 않는 인증 형식입니다.");
    }
}
