package toy.bookchat.bookchat.domain.user;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;

import java.util.ArrayList;
import java.util.List;

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
    @OneToMany(mappedBy = "user")
    private List<BookShelf> bookShelves = new ArrayList<>();

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
