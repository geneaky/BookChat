package toy.bookchat.bookchat.infrastructure.s3;

public enum SupportedFileExtension {
  WEBP("webp");

  private final String fileExtension;

  SupportedFileExtension(String fileExtension) {
    this.fileExtension = fileExtension;
  }

  public static void isSupport(String fileExtension) {
    for (SupportedFileExtension supportedFileExtension : SupportedFileExtension.values()) {
      if (supportedFileExtension.fileExtension.equals(fileExtension)) {
        return;
      }
    }
    throw new IllegalArgumentException("Not Supported File Extension");
  }

  public String getValue() {
    return this.fileExtension;
  }
}
