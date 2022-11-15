package toy.bookchat.bookchat.domain.agonyrecord;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.BaseEntity;
import toy.bookchat.bookchat.domain.agony.Agony;

@Getter
@Entity
public class AgonyRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    Agony agony;

    @Builder
    private AgonyRecord(Long id, String title, String content, Agony agony) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.agony = agony;
    }

    protected AgonyRecord() {
    }
}
