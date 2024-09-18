package toy.bookchat.bookchat.infrastructure.s3;

import javax.imageio.stream.ImageInputStream;

public interface ImageReaderAdapter {

  void setInput(ImageInputStream imageInputStream);

  int getWidth();

  int getHeight();
}
