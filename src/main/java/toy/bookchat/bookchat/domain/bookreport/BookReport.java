package toy.bookchat.bookchat.domain.bookreport;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.BaseEntity;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String title;
    String content;
    String hexColorCode;
    @OneToOne(mappedBy = "bookReport", fetch = FetchType.LAZY)
    BookShelf bookShelf;

    public BookReport(String title, String content, String hexColorCode, BookShelf bookShelf) {
        this.title = title;
        this.content = content;
        this.hexColorCode = hexColorCode;
        setBookShelf(bookShelf);
    }

    public void setBookShelf(BookShelf bookShelf) {
        this.bookShelf = bookShelf;
        bookShelf.setBookReport(this);
    }
}
