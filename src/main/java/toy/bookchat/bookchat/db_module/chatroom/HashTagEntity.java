package toy.bookchat.bookchat.db_module.chatroom;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import toy.bookchat.bookchat.db_module.BaseEntity;

@Entity
@Getter
@Table(name = "hash_tag")
public class HashTagEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "tag_name")
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
