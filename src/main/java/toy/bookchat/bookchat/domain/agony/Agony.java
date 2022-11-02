package toy.bookchat.bookchat.domain.agony;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.BaseEntity;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;

@Getter
@Entity
public class Agony extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String hexColorCode;
    @ManyToOne(fetch = FetchType.LAZY)
    private BookShelf bookShelf;

    @Builder
    private Agony(Long id, String title, String hexColorCode, BookShelf bookShelf) {
        this.id = id;
        this.title = title;
        this.hexColorCode = hexColorCode;
        this.bookShelf = bookShelf;
    }

    protected Agony() {
    }
}
