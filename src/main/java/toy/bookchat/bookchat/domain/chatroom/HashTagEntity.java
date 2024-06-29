package toy.bookchat.bookchat.domain.chatroom;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import toy.bookchat.bookchat.domain.BaseEntity;

@Entity
@Getter
@Table(name = "hash_tag")
public class HashTagEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tagName;

    protected HashTagEntity() {
    }

    private HashTagEntity(String tagName) {
        this.tagName = tagName;
    }

    public static HashTagEntity of(String tagName) {
        return new HashTagEntity(tagName);
    }
}
