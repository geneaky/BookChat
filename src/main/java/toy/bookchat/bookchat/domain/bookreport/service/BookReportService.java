package toy.bookchat.bookchat.domain.bookreport.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.bookchat.bookchat.domain.bookreport.service.dto.WriteBookReportRequestDto;
import toy.bookchat.bookchat.domain.user.User;

@Service
public class BookReportService {

    @Transactional
    public void writeReport(WriteBookReportRequestDto writeBookReportRequestDto, User user) {

    }
}
