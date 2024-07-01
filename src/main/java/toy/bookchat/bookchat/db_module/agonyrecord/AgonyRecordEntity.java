package toy.bookchat.bookchat.db_module.agonyrecord;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.db_module.BaseEntity;

@Getter
@Entity
@Table(name = "agony_record")
public class AgonyRecordEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String title;
    private String content;
    @Column(name = "agony_id", nullable = false)
    private Long agonyId;

    @Builder
    private AgonyRecordEntity(Long id, String title, String content, Long agonyId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.agonyId = agonyId;
    }

    protected AgonyRecordEntity() {
    }
}
