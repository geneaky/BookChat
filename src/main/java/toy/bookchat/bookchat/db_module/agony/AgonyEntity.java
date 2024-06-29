package toy.bookchat.bookchat.db_module.agony;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.db_module.BaseEntity;
import toy.bookchat.bookchat.db_module.bookshelf.BookShelfEntity;

@Getter
@Entity
@Table(name = "agony")
public class AgonyEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String hexColorCode;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_shelf_id")
    private BookShelfEntity bookShelfEntity;

    @Builder
    private AgonyEntity(Long id, String title, String hexColorCode, BookShelfEntity bookShelfEntity) {
        this.id = id;
        this.title = title;
        this.hexColorCode = hexColorCode;
        this.bookShelfEntity = bookShelfEntity;
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
