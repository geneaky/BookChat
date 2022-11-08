package toy.bookchat.bookchat.domain.agony.service.dto.request;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DeleteAgoniesRequest {

    @NotEmpty
    List<Long> agoniesIds;

    private DeleteAgoniesRequest(List<Long> agoniesIds) {
        this.agoniesIds = agoniesIds;
    }

    public static DeleteAgoniesRequest of(List<Long> agoniesIds) {
        return new DeleteAgoniesRequest(agoniesIds);
    }
}
