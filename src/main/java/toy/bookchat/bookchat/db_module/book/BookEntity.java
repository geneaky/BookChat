package toy.bookchat.bookchat.db_module.book;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.db_module.BaseEntity;

@Getter
@Entity
@Table(name = "book")
public class BookEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String isbn;
    private String title;
    private String publisher;
    private String bookCoverImageUrl;
    private LocalDate publishAt;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "book_author")
    private List<String> authors = new ArrayList<>();

    @Builder
    private BookEntity(Long id, String isbn, String title, List<String> authors, String publisher,
        String bookCoverImageUrl, LocalDate publishAt) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.bookCoverImageUrl = bookCoverImageUrl;
        this.publishAt = publishAt;
    }

    protected BookEntity() {
    }
}
