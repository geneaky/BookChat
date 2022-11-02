package toy.bookchat.bookchat.domain.user.exception;

public enum SupportedFileExtension {
    WEBP("webp");

    private final String fileExtension;

    SupportedFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public static boolean isSupport(String fileExtension) {
        for (SupportedFileExtension supportedFileExtension : SupportedFileExtension.values()) {
            if (supportedFileExtension.fileExtension.equals(fileExtension)) {
                return true;
            }
        }
        return false;
    }

    public String getValue() {
        return this.fileExtension;
    }
}
