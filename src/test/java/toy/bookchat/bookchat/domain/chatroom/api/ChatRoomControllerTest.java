package toy.bookchat.bookchat.domain.chatroom.api;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static toy.bookchat.bookchat.domain.common.AuthConstants.BEARER;
import static toy.bookchat.bookchat.domain.user.ROLE.USER;
import static toy.bookchat.bookchat.domain.user.ReadingTaste.ART;
import static toy.bookchat.bookchat.domain.user.ReadingTaste.DEVELOPMENT;
import static toy.bookchat.bookchat.security.oauth.OAuth2Provider.GOOGLE;
import static toy.bookchat.bookchat.security.oauth.OAuth2Provider.KAKAO;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import toy.bookchat.bookchat.domain.ControllerTestExtension;
import toy.bookchat.bookchat.domain.chatroom.service.ChatRoomService;
import toy.bookchat.bookchat.domain.chatroom.service.dto.request.CreateChatRoomRequest;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPrincipal;

@ChatRoomPresentationTest
class ChatRoomControllerTest extends ControllerTestExtension {

    public static final String JWT_TOKEN = BEARER + getTestToken();

    @MockBean
    private ChatRoomService chatRoomService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    private static String getTestToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "test");
        claims.put("name", "google123");
        claims.put("provider", GOOGLE);
        claims.put("email", "test@gmail.com");

        return Jwts.builder()
            .setClaims(claims)
            .signWith(HS256, "test")
            .compact();
    }

    private User getUser() {
        return User.builder()
            .id(1L)
            .email("test@gmail.com")
            .nickname("nickname")
            .role(USER)
            .name("testUser")
            .profileImageUrl("somethingImageUrl@naver.com")
            .defaultProfileImageType(1)
            .provider(KAKAO)
            .readingTastes(List.of(DEVELOPMENT, ART))
            .build();
    }

    private UserPrincipal getUserPrincipal() {
        User user = getUser();
        TokenPayload tokenPayload = TokenPayload.of(user.getId(), user.getName(),
            user.getNickname(),
            user.getEmail(), user.getProfileImageUrl(), user.getDefaultProfileImageType(),
            user.getRole());
        return UserPrincipal.create(tokenPayload);
    }

    @Test
    void 채팅방_생성_성공() throws Exception {
        CreateChatRoomRequest createChatRoomRequest = CreateChatRoomRequest.builder()
            .roomName("effective java 부수는 방")
            .roomSize(5)
            .bookId(1L)
            .build();

        mockMvc.perform(post("/v1/api/chatrooms")
                .header(AUTHORIZATION, JWT_TOKEN)
                .with(user(getUserPrincipal()))
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createChatRoomRequest)))
            .andExpect(status().isOk())
            .andDo(document("post-chatroom",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                requestFields(
                    fieldWithPath("roomName").type(STRING).description("채팅 방 이름"),
                    fieldWithPath("roomSize").type(NUMBER).description("채팅 방 인원 수"),
                    fieldWithPath("bookId").type(NUMBER).description("Book Id")
                )));

        verify(chatRoomService).createChatRoom(any(), any());
    }
}