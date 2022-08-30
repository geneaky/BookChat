package toy.bookchat.bookchat.domain.user;

import java.util.ArrayList;
import java.util.List;
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
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /* TODO: 2022-08-30 사용자 이름에 oauth2 회원번호 넣기
     */
    private String name;
    private String nickName;
    private String email;
    private String password;
    private String profileImageUrl;
    private ROLE role;
    @Enumerated(EnumType.STRING)
    private OAuth2Provider provider;
    @OneToMany(mappedBy = "user")
    private List<BookShelf> bookShelves = new ArrayList<>();

    @Builder
    private User(String name, String email, String password, String profileImageUrl, ROLE role,
        OAuth2Provider provider, String nickName) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
        this.provider = provider;
        this.nickName = nickName;
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
