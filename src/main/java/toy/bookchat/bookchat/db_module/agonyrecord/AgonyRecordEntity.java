package toy.bookchat.bookchat.db_module.agonyrecord;

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
import toy.bookchat.bookchat.db_module.agony.AgonyEntity;
import toy.bookchat.bookchat.domain.BaseEntity;

@Getter
@Entity
@Table(name = "agony_record")
public class AgonyRecordEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agony_id")
    AgonyEntity agonyEntity;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;

    @Builder
    private AgonyRecordEntity(Long id, String title, String content, AgonyEntity agonyEntity) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.agonyEntity = agonyEntity;
    }

    protected AgonyRecordEntity() {
    }
}
