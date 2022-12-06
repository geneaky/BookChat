package toy.bookchat.bookchat.domain.storage;

import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import toy.bookchat.bookchat.config.aws.ChatRoomStorageProperties;

@Service
public class ChatRoomStorageService implements StorageService {

    private final AmazonS3Client amazonS3Client;
    private final ChatRoomStorageProperties chatRoomStorageProperties;

    public ChatRoomStorageService(AmazonS3Client amazonS3Client,
        ChatRoomStorageProperties chatRoomStorageProperties) {
        this.amazonS3Client = amazonS3Client;
        this.chatRoomStorageProperties = chatRoomStorageProperties;
    }


    @Override
    public void upload(MultipartFile multipartFile, String fileName) {

    }

    @Override
    public String getFileUrl(String fileName) {
        return null;
    }

    @Override
    public String createFileName(String fileExtension, String uuidFileName, String currentTime) {
        return null;
    }
}
