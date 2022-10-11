package toy.bookchat.bookchat.domain.user;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.BaseEntity;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

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
    @Enumerated(EnumType.STRING)
    private OAuth2Provider provider;
    @OneToMany(mappedBy = "user") // TODO: 2022/10/10 delete cascade 설정
    private List<BookShelf> bookShelves = new ArrayList<>();
    @ElementCollection
    private List<ReadingTaste> readingTastes = new ArrayList<>();

    @Builder
    public User(String name, String email, String profileImageUrl, ROLE role,
        OAuth2Provider provider, String nickname, List<ReadingTaste> readingTastes,
        Integer defaultProfileImageType) {
        this.name = name;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
        this.provider = provider;
        this.nickname = nickname;
        this.readingTastes = readingTastes;
        this.defaultProfileImageType = defaultProfileImageType;
    }

    public void setBookShelf(BookShelf bookShelf) {
        this.getBookShelves().add(bookShelf);
        bookShelf.setUser(this);
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updateImageUrl(String imageUrl) {
        this.profileImageUrl = imageUrl;
    }
}
