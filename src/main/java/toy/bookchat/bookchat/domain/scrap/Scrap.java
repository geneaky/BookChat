package toy.bookchat.bookchat.domain.scrap;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Scrap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String scrapContent;
    @ManyToOne(fetch = FetchType.LAZY)
    private BookShelf bookShelf;
}
