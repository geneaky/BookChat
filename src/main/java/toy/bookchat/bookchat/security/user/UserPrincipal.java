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

    /* TODO: 2022-10-26 user entity바로 넘겨주면 user를 수정안하면 상관없는데
        user와 관련없는 도메인 예를 들어 book shelf service layer에서 public으로 열려있는
        user의 method를 사용해서 변경을 시도하려고하면 문제가 될 수 있을 것 같네
        user바로 반환하지말고 UserPayload? 이런식으로 해서 여기서 주거나 아니면
        security context에서 넣을때 payload의  값을 꺼내서 세팅한 authentication 구현체를
        넣어주는걸로 대체해야할듯
     */

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
