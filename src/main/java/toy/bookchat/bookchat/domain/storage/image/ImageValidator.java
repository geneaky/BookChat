package toy.bookchat.bookchat.domain.storage.image;

import java.io.IOException;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import toy.bookchat.bookchat.exception.user.ImageInputStreamException;

@Component
public class ImageValidator {

    private final ImageReaderAdapter imageReaderAdapter;

    public ImageValidator(ImageReaderAdapter imageReaderAdapter) {
        this.imageReaderAdapter = imageReaderAdapter;
    }

    private static void hasFileName(MultipartFile multipartFile) {
        if (!StringUtils.hasText(multipartFile.getOriginalFilename())) {
            throw new IllegalArgumentException("Can't Handle Blank Name File");
        }
    }

    private String getFileExtension(MultipartFile multipartFile) {
        return multipartFile.getOriginalFilename()
            .substring(multipartFile.getOriginalFilename().lastIndexOf(".") + 1);
    }

    public void hasValidImage(MultipartFile multipartFile, int widthLimit, int hegithLimit) {
        isNotEmptyFile(multipartFile);
        hasFileName(multipartFile);
        SupportedFileExtension.isSupport(getFileExtension(multipartFile));
        isValidFileSize(multipartFile, widthLimit, hegithLimit);
    }

    private void isNotEmptyFile(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new IllegalArgumentException("Can't Handle Empty File");
        }
    }

    private void isValidFileSize(MultipartFile multipartFile, int widthLimit, int heightLimit) {
        try {
            imageReaderAdapter.setInput(
                ImageIO.createImageInputStream(multipartFile.getInputStream()));
        } catch (IOException | NullPointerException exception) {
            throw new ImageInputStreamException(exception.getMessage());
        }

        if (imageReaderAdapter.getWidth() > widthLimit
            || imageReaderAdapter.getHeight() > heightLimit) {
            throw new IllegalArgumentException("Not Supplied File Size");
        }
    }

}
