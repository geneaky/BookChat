package toy.bookchat.bookchat.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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
import toy.bookchat.bookchat.domain.storage.image.ImageReaderAdapter;
import toy.bookchat.bookchat.domain.storage.image.ImageValidator;
import toy.bookchat.bookchat.domain.user.exception.ImageInputStreamException;

@ExtendWith(MockitoExtension.class)
class ImageValidatorTest {

    @Mock
    ImageReaderAdapter imageReaderAdapter;
    @InjectMocks
    ImageValidator imageValidator;

    @Test
    void 이미지_파일_확장자_요청시_반환() throws Exception {
        MultipartFile multipartFile = mock(MultipartFile.class);

        when(multipartFile.getOriginalFilename()).thenReturn("test.webp");

        String fileExtension = imageValidator.getFileExtension(multipartFile);

        assertThat(fileExtension).isEqualTo("webp");
    }

    @Test
    void 이미지_검증시_이미지가_없다면_false반환() throws Exception {
        assertThat(imageValidator.hasValidImage(null)).isFalse();
    }

    @Test
    void 이미지가_빈_파일일_경우_false반환() throws Exception {
        byte[] content = {};
        MockMultipartFile multipartFile = new MockMultipartFile("test", content);

        assertThat(imageValidator.hasValidImage(multipartFile)).isFalse();
    }

    @Test
    void 지원하지_않는_이미지_타입의_경우_예외발생() throws Exception {

        byte[] content = "TEST".getBytes();
        MockMultipartFile multipartFile = new MockMultipartFile("test", "test", "image/jpg",
            content);

        doThrow(NullPointerException.class).when(imageReaderAdapter).setInput(any());

        assertThatThrownBy(() -> {
            imageValidator.hasValidImage(multipartFile);
        }).isInstanceOf(ImageInputStreamException.class);
    }

    @Test
    void 지원하지_않는_이미지_사이즈의_경우_예외발생() throws Exception {
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getInputStream()).thenReturn(mock(InputStream.class));
        when(imageReaderAdapter.getWidth()).thenReturn(5000);

        assertThatThrownBy(() -> {
            imageValidator.hasValidImage(multipartFile);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 올바른_이미지의_경우_검증통과() throws Exception {
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getInputStream()).thenReturn(mock(InputStream.class));
        when(imageReaderAdapter.getHeight()).thenReturn(150);
        when(imageReaderAdapter.getWidth()).thenReturn(150);

        assertThat(imageValidator.hasValidImage(multipartFile)).isTrue();
    }
}