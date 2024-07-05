package toy.bookchat.bookchat.db_module.user;

import static javax.persistence.EnumType.STRING;
import static toy.bookchat.bookchat.domain.common.Status.INACTIVE;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;
import toy.bookchat.bookchat.db_module.BaseEntity;
import toy.bookchat.bookchat.domain.common.Status;
import toy.bookchat.bookchat.domain.user.ROLE;
import toy.bookchat.bookchat.domain.user.ReadingTaste;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@Getter
@Entity
@Table(name = "user")
@DynamicInsert
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * name은 [oauth2 provider+oauth2 member number]로 정의함
     */
    private String name;
    private String nickname;
    private String email;
    private String profileImageUrl;
    private ROLE role;
    private Integer defaultProfileImageType;
    @Enumerated(STRING)
    private OAuth2Provider provider;
    @ElementCollection
    @CollectionTable(name = "user_reading_tastes", joinColumns = @JoinColumn(name = "user_id"))
    private List<ReadingTaste> readingTastes = new ArrayList<>();
    @Enumerated(STRING)
    private Status status;

    @Builder
    private UserEntity(Long id, String name, String nickname, String email, String profileImageUrl,
        ROLE role, Integer defaultProfileImageType, OAuth2Provider provider,
        List<ReadingTaste> readingTastes, Status status) {
        this.id = id;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
        this.defaultProfileImageType = defaultProfileImageType;
        this.provider = provider;
        this.readingTastes = readingTastes;
        this.status = status;
    }

    protected UserEntity() {
    }

    public void updateImageUrl(String imageUrl) {
        this.profileImageUrl = imageUrl;
    }

    public void changeUserNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRoleName() {
        return this.role.getAuthority();
    }

    public void changeProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void inactive() {
        this.status = INACTIVE;
    }
}