package toy.bookchat.bookchat.domain.agony.service;

import org.springframework.stereotype.Service;
import toy.bookchat.bookchat.domain.agony.service.dto.CreateAgonyRecordRequestDto;
import toy.bookchat.bookchat.domain.user.User;

@Service
public class AgonyRecordService {

    public void storeAgonyRecord(CreateAgonyRecordRequestDto createAgonyRecordRequestDto, User user,
        Long bookId, Long agonyId) {
        
    }
}
