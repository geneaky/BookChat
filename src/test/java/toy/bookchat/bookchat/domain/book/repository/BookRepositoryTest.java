package toy.bookchat.bookchat.domain.book.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import toy.bookchat.bookchat.domain.RepositoryTest;
import toy.bookchat.bookchat.domain.book.Book;

@RepositoryTest
class BookRepositoryTest {

    @Autowired
    BookRepository bookRepository;

    @Test
    void 동시에_같은_책_입력_시도시_예외발생() throws Exception {
        int count = 50;
        CountDownLatch countDownLatch = new CountDownLatch(count);
        Book book = Book.builder()
            .isbn("1234")
            .publishAt(LocalDate.now())
            .build();

        AtomicReference<Throwable> exception = new AtomicReference<>();
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 0; i < count; i++) {
            executorService.execute(() -> {
                try {
                    bookRepository.saveAndFlush(book);
                } catch (Exception e) {
                    exception.set(e);
                }
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();

        assertThat(exception.get()).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void ISBN_출판일로_도서_조회_성공() throws Exception {
        String isbn = "1234567890";
        LocalDate publishAt = LocalDate.now();
        Book book = Book.builder()
            .isbn(isbn)
            .publishAt(publishAt)
            .build();

        bookRepository.save(book);

        Book findBook = bookRepository.findByIsbnAndPublishAt(isbn, publishAt).get();

        assertThat(findBook).isEqualTo(book);
    }
}