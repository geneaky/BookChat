package toy.bookchat.bookchat.exception.internalserver;

import static toy.bookchat.bookchat.exception.ErrorCode.IMAGE_UPLOAD_STORAGE;

public class ImageUploadToStorageException extends InternalServerException {

    public ImageUploadToStorageException() {
        super(IMAGE_UPLOAD_STORAGE, "이미지 업로드에 실패했습니다.");
    }
}
