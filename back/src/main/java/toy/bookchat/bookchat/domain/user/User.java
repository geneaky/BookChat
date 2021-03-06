package toy.bookchat.bookchat.domain.user;

import javax.persistence.*;

import lombok.*;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    private String profileImageUrl;
    private ROLE role;
    @Enumerated(EnumType.STRING)
    private OAuth2Provider provider;
    @OneToMany(mappedBy = "user")
    private List<BookShelf> bookShelves = new ArrayList<>();

    @Builder
    private User(String name, String email, String password, String profileImageUrl, ROLE role, OAuth2Provider provider) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
        this.provider = provider;
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
