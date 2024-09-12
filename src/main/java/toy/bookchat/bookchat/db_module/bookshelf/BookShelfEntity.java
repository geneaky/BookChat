package toy.bookchat.bookchat.db_module.bookshelf;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.db_module.BaseEntity;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.Star;

@Getter
@Entity
@Table(name = "book_shelf")
public class BookShelfEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "user_id", nullable = false)
  private Long userId;
  @Column(name = "book_id", nullable = false)
  private Long bookId;
  @Column(name = "pages")
  private Integer pages;
  @Column(name = "reading_status")
  @Enumerated(EnumType.STRING)
  private ReadingStatus readingStatus;
  @Column(name = "star")
  @Enumerated(EnumType.STRING)
  private Star star;

  @Builder
  private BookShelfEntity(Long id, Long userId, Long bookId, Integer pages, ReadingStatus readingStatus, Star star) {
    this.id = id;
    this.userId = userId;
    this.bookId = bookId;
    this.pages = pages;
    this.readingStatus = readingStatus;
    this.star = star;
  }

  protected BookShelfEntity() {
  }

  public void updateReadingStatus(ReadingStatus readingStatus) {
    this.readingStatus = readingStatus;
  }

  public void updateBy(BookShelf bookShelf) {
    this.star = bookShelf.getStar();
    this.pages = bookShelf.getPages();
    this.readingStatus = bookShelf.getReadingStatus();
  }
}
