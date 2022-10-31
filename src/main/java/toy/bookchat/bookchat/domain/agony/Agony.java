package toy.bookchat.bookchat.domain.agony;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.BaseEntity;
import toy.bookchat.bookchat.domain.bookshelf.BookShelf;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Agony extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    BookShelf bookShelf;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String hexColorCode;
    @OneToMany(mappedBy = "agony", orphanRemoval = true)
    private List<AgonyRecord> agonyRecords = new ArrayList<>();

    public Agony(Long id, String title, String hexColorCode,
        BookShelf bookShelf) {
        this.id = id;
        this.title = title;
        this.hexColorCode = hexColorCode;
        this.bookShelf = bookShelf;
    }

    public void setBookShelf(BookShelf bookShelf) {
        this.bookShelf = bookShelf;
        bookShelf.getAgonies().add(this);
    }

    public void addAgonyRecord(AgonyRecord agonyRecord) {
        this.agonyRecords.add(agonyRecord);
        agonyRecord.setAgony(this);
    }
}
