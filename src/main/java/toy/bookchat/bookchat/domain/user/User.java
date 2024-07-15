package toy.bookchat.bookchat.domain.user;

import lombok.Builder;
import lombok.Getter;

@Getter
public class User {

    private Long id;

    @Builder
    private User(Long id) {
        this.id = id;
    }
}
