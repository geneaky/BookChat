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
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book {

    @Id
    @GeneratedValue
    private Long id;
    private String isbn;
    private String title;
    @ElementCollection
    private List<String> authors = new ArrayList<>();
    private String publisher;
    private String bookCoverImageUrl;
    @OneToMany
    private List<BookShelf> bookShelves = new ArrayList<>();
}
