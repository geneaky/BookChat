package toy.bookchat.bookchat.domain.chat.api.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageDto {

    @NotBlank
    private String message;

    @Builder
    private MessageDto(String message) {
        this.message = message;
    }
}
