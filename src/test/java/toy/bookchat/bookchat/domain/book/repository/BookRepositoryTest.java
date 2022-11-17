package toy.bookchat.bookchat.domain.book.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.util.ErrorHandler;
import toy.bookchat.bookchat.domain.RepositoryTest;
import toy.bookchat.bookchat.domain.book.Book;

@RepositoryTest
class BookRepositoryTest {

    @Autowired
    BookRepository bookRepository;

    @Test
    void 동시에_같은_책_입력_시도시_예외발생() throws Exception {
        int maxSize = 3;
        CountDownLatch countDownLatch = new CountDownLatch(maxSize);
        Book book = Book.builder()
            .isbn("1234")
            .build();

        AtomicReference<Throwable> exception = new AtomicReference<>();
        ErrorHandler errorHandler = t -> {
            exception.set(t);
            countDownLatch.countDown();
        };

        List<BookRepositoryTestWorker> workers = Stream.generate(
                () -> new BookRepositoryTestWorker(countDownLatch, errorHandler, book))
            .limit(maxSize)
            .collect(Collectors.toList());

        workers.forEach(worker -> new Thread(worker).start());
        countDownLatch.await();

        assertThat(exception.get()).isInstanceOf(DataIntegrityViolationException.class);
    }

    private class BookRepositoryTestWorker implements Runnable {

        private final CountDownLatch countDownLatch;
        private final ErrorHandler errorHandler;

        private final Book book;

        private BookRepositoryTestWorker(CountDownLatch countDownLatch,
            ErrorHandler errorHandler, Book book) {
            this.countDownLatch = countDownLatch;
            this.errorHandler = errorHandler;
            this.book = book;
        }

        @Override
        public void run() {
            try {
                bookRepository.saveAndFlush(this.book);
                this.countDownLatch.countDown();
            } catch (Exception e) {
                errorHandler.handleError(e);
            }
        }
    }

}