package toy.bookchat.bookchat.exception.internalserver;

import static toy.bookchat.bookchat.exception.ErrorCode.IMAGE_INPUT_STREAM;

public class ImageInputStreamException extends InternalServerException {

    public ImageInputStreamException() {
        super(IMAGE_INPUT_STREAM, "이미지 처리에 실패했습니다.");
    }
}
