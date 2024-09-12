package toy.bookchat.bookchat.db_module.agony;

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
@Table(name = "agony")
public class AgonyEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "title")
  private String title;
  @Column(name = "hex_color_code")
  private String hexColorCode;
  @Column(name = "book_shelf_id", nullable = false)
  private Long bookShelfId;

  @Builder
  private AgonyEntity(Long id, String title, String hexColorCode, Long bookShelfId) {
    this.id = id;
    this.title = title;
    this.hexColorCode = hexColorCode;
    this.bookShelfId = bookShelfId;
  }

  protected AgonyEntity() {
  }

  public void changeTitle(String agonyTitle) {
    this.title = agonyTitle;
  }

  public void changeHexColorCode(String agonyColor) {
    this.hexColorCode = agonyColor;
  }
}
