package toy.bookchat.bookchat.domain.storage.image;

import javax.imageio.stream.ImageInputStream;

public interface ImageReaderAdapter {

    void setInput(ImageInputStream imageInputStream);

    int getWidth();

    int getHeight();
}
