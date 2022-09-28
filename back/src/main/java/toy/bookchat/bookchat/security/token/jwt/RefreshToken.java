package toy.bookchat.bookchat.security.token.jwt;

import lombok.*;
import toy.bookchat.bookchat.domain.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private String refreshToken;

    @Builder
    public RefreshToken(Long id, String userName, String refreshToken) {
        this.id = id;
        this.userName = userName;
        this.refreshToken = refreshToken;
    }
}
