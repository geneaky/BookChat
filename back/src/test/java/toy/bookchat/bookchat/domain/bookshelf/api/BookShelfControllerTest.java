package toy.bookchat.bookchat.domain.bookshelf.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import toy.bookchat.bookchat.domain.AuthenticationTestExtension;
import toy.bookchat.bookchat.domain.bookshelf.ReadingStatus;
import toy.bookchat.bookchat.domain.bookshelf.dto.BookShelfRequestDto;
import toy.bookchat.bookchat.domain.bookshelf.service.BookShelfService;
import toy.bookchat.bookchat.security.user.UserPrincipal;

@WebMvcTest(controllers = BookShelfController.class,
    includeFilters = @ComponentScan.Filter(classes = {EnableWebSecurity.class}))
@AutoConfigureRestDocs
public class BookShelfControllerTest extends AuthenticationTestExtension {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BookShelfService bookShelfService;

    @Autowired
    private MockMvc mockMvc;

    private UserPrincipal getUserPrincipal() {
        List<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_USER")
        );
        UserPrincipal userPrincipal = new UserPrincipal(1L, "test@gmail.com", "password",
            "testUser", "somethingImageUrl.com", authorities);

        return userPrincipal;
    }

    @Test
    public void 읽고_있는_책_등록_성공() throws Exception {

        BookShelfRequestDto bookShelfRequestDto = BookShelfRequestDto.builder()
            .isbn("124151214")
            .title("effectiveJava")
            .author(List.of("Joshua"))
            .publisher("oreilly")
            .bookCoverImageUrl("bookCoverImage.com")
            .readingStatus(ReadingStatus.READING)
            .build();

        MvcResult mvcResult = mockMvc.perform(post("/v1/api/bookshelf/books")
                .content(objectMapper.writeValueAsString(bookShelfRequestDto))
                .contentType(MediaType.APPLICATION_JSON)
                .with(user(getUserPrincipal())))
            .andExpect(status().isCreated())
            .andDo(document("bookshelf",
                requestFields(fieldWithPath("isbn").description("isbn"),
                    fieldWithPath("title").description("title"),
                    fieldWithPath("author").description("author"),
                    fieldWithPath("publisher").description("publisher"),
                    fieldWithPath("bookCoverImageUrl").description("bookCoverImageUrl"),
                    fieldWithPath("readingStatus").description("readingStatus"))))
            .andReturn();

        verify(bookShelfService).putBookOnBookShelf(any(BookShelfRequestDto.class));
    }
}
