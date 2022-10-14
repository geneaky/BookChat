package toy.bookchat.bookchat.domain.storage.image;

import static toy.bookchat.bookchat.domain.user.service.dto.SupportedFileExtension.WEBP;

import com.luciad.imageio.webp.WebPImageReaderSpi;
import java.io.IOException;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.springframework.stereotype.Component;
import toy.bookchat.bookchat.domain.user.exception.ImageInputStreamException;

@Component
public class ImageReaderAdapterImpl implements ImageReaderAdapter {

    private final ImageReader readerInstance;

    public ImageReaderAdapterImpl() {
        try {
            this.readerInstance = new WebPImageReaderSpi().createReaderInstance(WEBP.getValue());
        } catch (IOException exception) {
            throw new ImageInputStreamException(exception.getMessage(), exception.getCause());
        }
    }

    @Override
    public void setInput(ImageInputStream imageInputStream) {
        readerInstance.setInput(imageInputStream);
    }

    @Override
    public int getWidth() {
        try {
            return readerInstance.getWidth(0);
        } catch (IOException exception) {
            throw new ImageInputStreamException(exception.getMessage(), exception.getCause());
        }
    }

    @Override
    public int getHeight() {
        try {
            return readerInstance.getHeight(0);
        } catch (IOException exception) {
            throw new ImageInputStreamException(exception.getMessage(), exception.getCause());
        }
    }
}
