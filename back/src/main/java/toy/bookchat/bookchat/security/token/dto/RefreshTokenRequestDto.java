package toy.bookchat.bookchat.security.token.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RefreshTokenRequestDto {

    @NotBlank
    private String refreshToken;

    @Builder
    public RefreshTokenRequestDto(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}



