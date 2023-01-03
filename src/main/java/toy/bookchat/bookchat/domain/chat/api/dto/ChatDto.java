package toy.bookchat.bookchat.domain.chat.api.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatDto {

    private String message;

    @Builder
    private ChatDto(String message) {
        this.message = message;
    }
}
