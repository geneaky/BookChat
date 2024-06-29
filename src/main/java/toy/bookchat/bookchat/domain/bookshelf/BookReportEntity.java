package toy.bookchat.bookchat.domain.bookshelf;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.BaseEntity;

@Getter
@Entity
@Table(name = "book_report")
public class BookReportEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    @OneToOne(mappedBy = "bookReportEntity", fetch = FetchType.LAZY)
    private BookShelfEntity bookShelfEntity;

    @Builder
    private BookReportEntity(Long id, String title, String content, BookShelfEntity bookShelfEntity) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.bookShelfEntity = bookShelfEntity;
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
