package toy.bookchat.bookchat.domain.storage.image;

import java.io.InputStream;

public interface ImageReaderAdapter {

    void setInput(InputStream inputStream);

    int getWidth();

    int getHeight();
}
