package toy.bookchat.bookchat.domain.chatroom;

import lombok.Builder;
import lombok.Getter;

@Getter
public class HashTag {

  private final Long id;
  private final String tagName;

  @Builder
  private HashTag(Long id, String tagName) {
    this.id = id;
    this.tagName = tagName;
  }

  public HashTag withId(Long id) {
    return HashTag.builder()
        .id(id)
        .tagName(this.tagName)
        .build();
  }
}
