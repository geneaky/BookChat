package toy.bookchat.bookchat.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Getter
@RequiredArgsConstructor
public enum ROLE implements GrantedAuthority {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");


    private final String roleName;

    @Override
    public String getAuthority() {
        return ROLE.USER.toString();
    }
}
