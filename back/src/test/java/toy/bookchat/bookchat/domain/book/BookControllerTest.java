package toy.bookchat.bookchat.domain.book;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import toy.bookchat.bookchat.domain.book.api.BookController;
import toy.bookchat.bookchat.domain.book.dto.BookDto;
import toy.bookchat.bookchat.domain.book.service.BookSearchService;
import toy.bookchat.bookchat.domain.user.repository.UserRepository;
import toy.bookchat.bookchat.security.handler.CustomAuthenticationFailureHandler;
import toy.bookchat.bookchat.security.handler.CustomAuthenticationSuccessHandler;
import toy.bookchat.bookchat.security.handler.RestAuthenticationEntryPoint;
import toy.bookchat.bookchat.security.jwt.JwtAuthenticationFilter;
import toy.bookchat.bookchat.security.jwt.JwtTokenProvider;
import toy.bookchat.bookchat.security.oauth.CustomOAuth2UserService;
import toy.bookchat.bookchat.security.oauth.HttpCookieOAuth2AuthorizationRequestRepository;
import toy.bookchat.bookchat.security.user.UserPrincipal;

@WebMvcTest(controllers = BookController.class,
    includeFilters = @ComponentScan.Filter(classes = {EnableWebSecurity.class}))
@Import({JwtAuthenticationFilter.class, RestAuthenticationEntryPoint.class})
@AutoConfigureRestDocs
public class BookControllerTest {

    @MockBean
    CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    @MockBean
    CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    @MockBean
    CustomOAuth2UserService customOAuth2UserService;
    @MockBean
    JwtTokenProvider jwtTokenProvider;
    @MockBean
    UserRepository userRepository;
    @MockBean
    HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    @MockBean
    BookSearchService bookSearchService;

    @Autowired
    ObjectMapper objectMapper;

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
    public void 로그인하지_않은_사용자_요청_401() throws Exception {
        mockMvc.perform(get("/v1/api/books")
                .param("isbn", "234134"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    public void 로그인한_사용자_요청_200() throws Exception {
        BookDto bookDto = BookDto.builder()
            .isbn("213123")
            .title("effectiveJava")
            .author("joshua")
            .bookCoverImageUrl("testCoverImageUrl")
            .build();

        when(bookSearchService.search(anyString())).thenReturn(bookDto);
        mockMvc.perform(get("/v1/api/books")
                .param("isbn", "213123")
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk());
    }

    @Test
    public void 사용자가_isbn으로_책_검색_요청시_성공() throws Exception {
        BookDto bookDto = BookDto.builder()
            .isbn("1231513")
            .title("effectiveJava")
            .author("Joshua")
            .bookCoverImageUrl("imageUrl.com")
            .build();

        when(bookSearchService.search("1231513")).thenReturn(bookDto);

        String result = objectMapper.writeValueAsString(bookDto);

        MvcResult mvcResult = mockMvc.perform(get("/v1/api/books")
                .param("isbn", "1231513")
                .with(user(getUserPrincipal())))
            .andExpect(status().isOk())
            .andReturn();

        Assertions.assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo(result);
    }

}
