package toy.bookchat.bookchat.db_module.scrap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "scrap")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScrapEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String scrapContent;
  @Column(name = "book_shelf_id", nullable = false)
  private Long bookShelfId;

  @Builder
  private ScrapEntity(Long id, String scrapContent, Long bookShelfId) {
    this.id = id;
    this.scrapContent = scrapContent;
    this.bookShelfId = bookShelfId;
  }
}
