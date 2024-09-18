package toy.bookchat.bookchat.infrastructure.s3;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ImageValidatorTest {

  @Mock
  ImageReaderAdapter imageReaderAdapter;
  @InjectMocks
  ImageValidator imageValidator;

  @Test
  void 이미지_검증시_이미지가_없다면_예외발생() throws Exception {
    assertThatThrownBy(() -> {
      imageValidator.hasValidImage(null, 200, 200);
    }).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void 이미지가_빈_파일일_경우_false반환() throws Exception {
    byte[] content = {};
    MockMultipartFile multipartFile = new MockMultipartFile("test", content);
    assertThatThrownBy(() -> {
      imageValidator.hasValidImage(multipartFile, 200, 200);
    }).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void 지원하지_않는_이미지_타입의_경우_예외발생() throws Exception {
    MockMultipartFile multipartFile = new MockMultipartFile("test", "test", "image/jpg",
        "TEST".getBytes());

    assertThatThrownBy(() -> {
      imageValidator.hasValidImage(multipartFile, 200, 200);
    }).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void 지원하지_않는_이미지_사이즈의_경우_예외발생() throws Exception {
    MultipartFile multipartFile = mock(MultipartFile.class);
    when(multipartFile.getInputStream()).thenReturn(mock(InputStream.class));
    when(multipartFile.getOriginalFilename()).thenReturn("test.webp");
    when(imageReaderAdapter.getWidth()).thenReturn(5000);

    assertThatThrownBy(() -> {
      imageValidator.hasValidImage(multipartFile, 200, 200);
    }).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void 올바른_이미지의_경우_검증통과() throws Exception {
    MultipartFile multipartFile = mock(MultipartFile.class);
    when(multipartFile.getInputStream()).thenReturn(mock(InputStream.class));
    when(multipartFile.getOriginalFilename()).thenReturn("test.webp");
    when(imageReaderAdapter.getHeight()).thenReturn(200);
    when(imageReaderAdapter.getWidth()).thenReturn(200);
    imageValidator.hasValidImage(multipartFile, 200, 200);
    assertThatNoException();
  }
}