package toy.bookchat.bookchat.domain.book;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String isbn;
    private String title;
    @ElementCollection
    private List<String> authors = new ArrayList<>();
    private String publisher;
    private String bookCoverImageUrl;
    @OneToMany(mappedBy = "book")
    private List<BookShelf> bookShelves = new ArrayList<>();

    @Builder
    private Book(String isbn, String title, List<String> authors, String publisher, String bookCoverImageUrl) {
        this.isbn = isbn;
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.bookCoverImageUrl = bookCoverImageUrl;
    }

    public void setBookShelf(BookShelf bookShelf) {
        this.getBookShelves().add(bookShelf);
        bookShelf.setBook(this);
    }
}
