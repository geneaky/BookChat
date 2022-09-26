package toy.bookchat.bookchat.security.user;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import toy.bookchat.bookchat.domain.user.User;

@Getter
public class UserPrincipal implements UserDetails, OAuth2User {

    private final User user;
    private final Long id;
    private final String email;
    private final String userName;
    private final String profileImageUri;
    private final Collection<? extends GrantedAuthority> authorities;
    @Setter
    private Map<String, Object> attributes;
    private Integer defaultProfileImageType;
    private String nickname;

    public UserPrincipal(Long id, String email, String userName,
        String nickname, String profileImageUri, Integer defaultProfileImageType,
        Collection<? extends GrantedAuthority> authorities, User user) {
        this.user = user;
        this.id = id;
        this.email = email;
        this.userName = userName;
        this.nickname = nickname;
        this.profileImageUri = profileImageUri;
        this.defaultProfileImageType = defaultProfileImageType;
        this.authorities = authorities;
    }

    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        return new UserPrincipal(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getNickname(),
            user.getProfileImageUrl(),
            user.getDefaultProfileImageType(),
            authorities,
            user
        );
    }

    public static UserPrincipal create(User user, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        userPrincipal.setAttributes(attributes);
        return userPrincipal;
    }

    public User getUser() {
        return user;
    }

    public Integer getDefaultProfileImageType() {
        return defaultProfileImageType;
    }

    public String getNickname() {
        return nickname;
    }

    @Override
    public String getName() {
        return this.userName;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
