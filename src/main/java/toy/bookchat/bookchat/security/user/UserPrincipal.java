package toy.bookchat.bookchat.security.user;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class UserPrincipal implements UserDetails, Principal {

    private final TokenPayload tokenPayload;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(TokenPayload tokenPayload,
        Collection<? extends GrantedAuthority> authorities) {
        this.tokenPayload = tokenPayload;
        this.authorities = authorities;
    }

    public static UserPrincipal create(TokenPayload tokenPayload) {
        List<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority(tokenPayload.getUserRole().getAuthority())
        );

        return new UserPrincipal(tokenPayload, authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    @Deprecated
    public String getName() {
        return this.tokenPayload.getUserNickname();
    }

    @Override
    @Deprecated
    public String getUsername() {
        return this.tokenPayload.getUserName();
    }

    @Override
    @Deprecated
    public String getPassword() {
        return null;
    }

    @Override
    @Deprecated
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @Deprecated
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @Deprecated
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @Deprecated
    public boolean isEnabled() {
        return true;
    }
}
