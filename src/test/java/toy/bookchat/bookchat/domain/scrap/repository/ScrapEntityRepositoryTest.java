package toy.bookchat.bookchat.domain.scrap.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import toy.bookchat.bookchat.db_module.book.BookEntity;
import toy.bookchat.bookchat.db_module.book.repository.BookRepository;
import toy.bookchat.bookchat.db_module.bookshelf.BookShelfEntity;
import toy.bookchat.bookchat.db_module.bookshelf.repository.BookShelfRepository;
import toy.bookchat.bookchat.db_module.scrap.repository.ScrapRepository;
import toy.bookchat.bookchat.domain.RepositoryTest;
import toy.bookchat.bookchat.db_module.scrap.ScrapEntity;
import toy.bookchat.bookchat.domain.scrap.service.dto.response.ScrapResponse;
import toy.bookchat.bookchat.db_module.user.UserEntity;
import toy.bookchat.bookchat.db_module.user.repository.UserRepository;

class ScrapEntityRepositoryTest extends RepositoryTest {

    @Autowired
    private ScrapRepository scrapRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookShelfRepository bookShelfRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void 사용자_서재_스크랩_조회_성공() throws Exception {
        UserEntity userEntity = UserEntity.builder().build();
        userRepository.save(userEntity);

        BookEntity bookEntity = BookEntity.builder()
            .isbn("1231241")
            .publishAt(LocalDate.now())
            .build();
        bookRepository.save(bookEntity);

        BookShelfEntity bookShelfEntity = BookShelfEntity.builder()
            .bookEntity(bookEntity)
            .userEntity(userEntity)
            .build();
        bookShelfRepository.save(bookShelfEntity);

        ScrapEntity scrapEntity = ScrapEntity.builder()
            .bookShelfEntity(bookShelfEntity)
            .scrapContent("content1")
            .build();
        scrapRepository.save(scrapEntity);

        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by("id").ascending());

        Slice<ScrapResponse> scrapResponseSlice = scrapRepository.findScraps(bookShelfEntity.getId(),
            null,
            pageRequest, userEntity.getId());

        assertThat(scrapResponseSlice.hasNext()).isFalse();
    }

    @Test
    void 사용자_스크랩_단_건_조회_성공() throws Exception {
        UserEntity userEntity = UserEntity.builder().build();
        userRepository.save(userEntity);

        BookEntity bookEntity = BookEntity.builder()
            .isbn("1231241")
            .publishAt(LocalDate.now())
            .build();
        bookRepository.save(bookEntity);

        BookShelfEntity bookShelfEntity = BookShelfEntity.builder()
            .bookEntity(bookEntity)
            .userEntity(userEntity)
            .build();
        bookShelfRepository.save(bookShelfEntity);

        ScrapEntity scrapEntity = ScrapEntity.builder()
            .bookShelfEntity(bookShelfEntity)
            .scrapContent("content1")
            .build();
        scrapRepository.save(scrapEntity);

        Optional<ScrapEntity> findScrap = scrapRepository.findUserScrap(scrapEntity.getId(), userEntity.getId());

        assertThat(findScrap).isPresent();
    }
}