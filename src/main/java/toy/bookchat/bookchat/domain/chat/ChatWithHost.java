package toy.bookchat.bookchat.domain.chat;

import lombok.Builder;
import lombok.Getter;
import toy.bookchat.bookchat.domain.participant.Host;

@Getter
public class ChatWithHost {

  private Chat chat;
  private Host host;

  @Builder
  private ChatWithHost(Chat chat, Host host) {
    this.chat = chat;
    this.host = host;
  }
}
