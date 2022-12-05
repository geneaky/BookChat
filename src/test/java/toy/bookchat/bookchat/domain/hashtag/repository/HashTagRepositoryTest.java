package toy.bookchat.bookchat.domain.hashtag.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import toy.bookchat.bookchat.domain.RepositoryTest;
import toy.bookchat.bookchat.domain.hashtag.HashTag;

@RepositoryTest
class HashTagRepositoryTest {

    @Autowired
    HashTagRepository hashTagRepository;

    @Test
    void 해시태그_저장_성공() throws Exception {
        String tagName = "test";
        HashTag hashTag = HashTag.of(tagName);
        hashTagRepository.save(hashTag);
        HashTag findHashTag = hashTagRepository.findByTagName(tagName).get();

        assertThat(findHashTag).isEqualTo(hashTag);
    }
}