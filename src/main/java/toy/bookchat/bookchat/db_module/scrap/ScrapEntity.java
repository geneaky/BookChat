package toy.bookchat.bookchat.db_module.scrap;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.db_module.bookshelf.BookShelfEntity;

@Entity
@Getter
@Table(name = "scrap")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScrapEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String scrapContent;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_shelf_id")
    private BookShelfEntity bookShelfEntity;

    @Builder
    private ScrapEntity(Long id, String scrapContent, BookShelfEntity bookShelfEntity) {
        this.id = id;
        this.scrapContent = scrapContent;
        this.bookShelfEntity = bookShelfEntity;
    }
}
