package toy.bookchat.bookchat.db_module.bookreport;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.db_module.BaseEntity;

@Getter
@Entity
@Table(name = "book_report")
public class BookReportEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "title")
  private String title;
  @Column(name = "content")
  private String content;
  @Column(name = "book_shelf_id", nullable = false)
  private Long bookShelfId;

  @Builder
  private BookReportEntity(Long id, String title, String content, Long bookShelfId) {
    this.id = id;
    this.title = title;
    this.content = content;
    this.bookShelfId = bookShelfId;
  }

  protected BookReportEntity() {
  }

  public void reviseTitle(String title) {
    this.title = title;
  }

  public void reviseContent(String content) {
    this.content = content;
  }
}
