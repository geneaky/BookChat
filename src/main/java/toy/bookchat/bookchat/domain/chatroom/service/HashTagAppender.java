package toy.bookchat.bookchat.domain.chatroom.service;

import java.util.Optional;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.db_module.chatroom.HashTagEntity;
import toy.bookchat.bookchat.db_module.chatroom.repository.HashTagRepository;
import toy.bookchat.bookchat.domain.chatroom.HashTag;

@Component
public class HashTagAppender {

  private final HashTagRepository hashTagRepository;

  public HashTagAppender(HashTagRepository hashTagRepository) {
    this.hashTagRepository = hashTagRepository;
  }

  public HashTag append(HashTag hashTag) {
    Optional<HashTagEntity> optionalHashTagEntity = hashTagRepository.findByTagName(hashTag.getTagName());
    if (optionalHashTagEntity.isEmpty()) {
      HashTagEntity hashTagEntity = HashTagEntity.of(hashTag.getTagName());
      hashTagRepository.save(hashTagEntity);
      return hashTag.withId(hashTagEntity.getId());
    }

    return hashTag.withId(optionalHashTagEntity.get().getId());
  }
}
