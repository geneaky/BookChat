package toy.bookchat.bookchat.domain.chatroom;

import java.util.List;
import lombok.Getter;

@Getter
public class HashTags {

  private final List<HashTag> list;

  public HashTags(List<HashTag> list) {
    this.list = list;
  }
}
