package toy.bookchat.bookchat.domain.hashtag;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import toy.bookchat.bookchat.domain.BaseEntity;

@Entity
public class HashTag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String tagName;

    protected HashTag() {
    }

    private HashTag(String tagName) {
        this.tagName = tagName;
    }

    public static HashTag of(String tagName) {
        return new HashTag(tagName);
    }
}
