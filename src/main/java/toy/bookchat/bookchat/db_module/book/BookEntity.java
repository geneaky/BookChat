package toy.bookchat.bookchat.db_module.book;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
  @Column(name = "isbn")
  private String isbn;
  @Column(name = "title")
  private String title;
  @Column(name = "publisher")
  private String publisher;
  @Column(name = "book_cover_image_url")
  private String bookCoverImageUrl;
  @Column(name = "publish_at")
  private LocalDate publishAt;
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "book_authors", joinColumns = @JoinColumn(name = "book_id"))
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
