package toy.bookchat.bookchat.domain.user;

import org.springframework.security.core.GrantedAuthority;

public enum ROLE implements GrantedAuthority {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final String authority;

    ROLE(String authority) {
        this.authority = authority;
    }

    public static ROLE value(String authority) {
        for (ROLE role : ROLE.values()) {
            if (role.authority.equals(authority)) {
                return role;
            }
        }

        return null;
    }

    @Override
    public String getAuthority() {
        return this.authority;
    }
}
