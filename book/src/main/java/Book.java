import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(
        columnNames = {"isbn", "publishAt"}
    )
})
@Getter
public class Book extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String isbn;
    private String title;
    private String publisher;
    private String bookCoverImageUrl;
    private LocalDate publishAt;
    @ElementCollection
    private List<String> authors = new ArrayList<>();

    @Builder
    private Book(Long id, String isbn, String title, List<String> authors, String publisher,
        String bookCoverImageUrl, LocalDate publishAt) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.bookCoverImageUrl = bookCoverImageUrl;
        this.publishAt = publishAt;
    }

    protected Book() {
    }
}
