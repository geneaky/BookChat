package toy.bookchat.bookchat.domain.chatroom.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import toy.bookchat.bookchat.db_module.chatroom.repository.HashTagRepository;
import toy.bookchat.bookchat.domain.RepositoryTest;
import toy.bookchat.bookchat.db_module.chatroom.HashTagEntity;

class HashTagEntityRepositoryTest extends RepositoryTest {

    @Autowired
    HashTagRepository hashTagRepository;

    @Test
    void 해시태그_저장_성공() throws Exception {
        String tagName = "test";
        HashTagEntity hashTagEntity = HashTagEntity.of(tagName);
        hashTagRepository.save(hashTagEntity);
        HashTagEntity findHashTagEntity = hashTagRepository.findByTagName(tagName).get();

        assertThat(findHashTagEntity).isEqualTo(hashTagEntity);
    }
}