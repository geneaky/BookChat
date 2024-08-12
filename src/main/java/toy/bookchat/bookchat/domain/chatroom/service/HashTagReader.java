package toy.bookchat.bookchat.domain.chatroom.service;

import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.chatroom.repository.HashTagRepository;

@Component
public class HashTagReader {

  private final HashTagRepository hashTagRepository;

  public HashTagReader(HashTagRepository hashTagRepository) {
    this.hashTagRepository = hashTagRepository;
  }
}
