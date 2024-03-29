package toy.bookchat.bookchat.domain.bookshelf;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.BaseEntity;

@Entity
@Getter
public class BookReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    @OneToOne(mappedBy = "bookReport", fetch = FetchType.LAZY)
    private BookShelf bookShelf;

    @Builder
    private BookReport(Long id, String title, String content, BookShelf bookShelf) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.bookShelf = bookShelf;
    }

    protected BookReport() {
    }

    public void reviseTitle(String title) {
        this.title = title;
    }

    public void reviseContent(String content) {
        this.content = content;
    }
}
