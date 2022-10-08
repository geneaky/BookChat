package toy.bookchat.bookchat.domain.storage.image;

import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import toy.bookchat.bookchat.domain.user.exception.ImageInputStreamException;

@Component
public class ImageValidator {

    public static final int WIDTH_LIMIT = 200;
    public static final int HEIGHT_LIMIT = 200;

    private final ImageReaderAdapter imageReaderAdapter;

    public ImageValidator(ImageReaderAdapter imageReaderAdapter) {
        this.imageReaderAdapter = imageReaderAdapter;
    }

    public String getFileExtension(MultipartFile multipartFile) {
        return multipartFile.getOriginalFilename()
            .substring(multipartFile.getOriginalFilename().lastIndexOf(".") + 1);
    }

    public boolean hasValidImage(MultipartFile multipartFile) {

        if (multipartFile == null || multipartFile.isEmpty()) {
            return false;
        }

        try {
            imageReaderAdapter.setInput(multipartFile.getInputStream());
        } catch (IOException | NullPointerException exception) {
            throw new ImageInputStreamException(exception.getMessage(), exception.getCause());
        }

        return isValidFileSize();
    }

    private boolean isValidFileSize() {
        if (imageReaderAdapter.getWidth() <= WIDTH_LIMIT
            && imageReaderAdapter.getHeight() <= HEIGHT_LIMIT) {
            return true;
        }

        throw new IllegalArgumentException("Not Supplied File Size");
    }
}
