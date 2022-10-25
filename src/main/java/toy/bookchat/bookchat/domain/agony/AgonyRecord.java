package toy.bookchat.bookchat.domain.agony;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.bookchat.bookchat.domain.BaseEntity;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AgonyRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    private String hexColorCode;
    @ManyToOne(fetch = FetchType.LAZY)
    Agony agony;

    public AgonyRecord(String title, String content, String hexColorCode,
        Agony agony) {
        this.title = title;
        this.content = content;
        this.hexColorCode = hexColorCode;
        setAgony(agony);
    }

    public void setAgony(Agony agony) {
        this.agony = agony;
        agony.getAgonyRecords().add(this);
    }
}
