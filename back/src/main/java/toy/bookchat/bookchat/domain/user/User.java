package toy.bookchat.bookchat.domain.user;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String email;
    private String password;
    private String profileImageUrl;
    private ROLE role;
    @Enumerated(EnumType.STRING)
    private OAuth2Provider provider;


    public void updateEmail(String email) {
        this.email = email;
    }

    public void updateImageUrl(String imageUrl) {
        this.profileImageUrl = imageUrl;
    }
}
