package toy.bookchat.bookchat.domain.bookshelf;

import javax.persistence.*;

import lombok.*;
import toy.bookchat.bookchat.domain.book.Book;
import toy.bookchat.bookchat.domain.user.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookShelf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    private ReadingStatus readingStatus;

    @Builder
    private BookShelf(Book book, User user, ReadingStatus readingStatus) {
        this.book = book;
        this.user = user;
        this.readingStatus = readingStatus;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
