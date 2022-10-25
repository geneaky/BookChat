package toy.bookchat.bookchat.domain.agony;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Agony {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String hexColorCode;
    @ManyToOne(fetch = FetchType.LAZY)
    BookShelf bookShelf;

    public Agony(String title, String hexColorCode,
        BookShelf bookShelf) {
        this.title = title;
        this.hexColorCode = hexColorCode;
        setBookShelf(bookShelf);
    }

    public void setBookShelf(BookShelf bookShelf) {
        this.bookShelf = bookShelf;
        bookShelf.getAgonies().add(this);
    }
}
