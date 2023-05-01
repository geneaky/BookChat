package toy.bookchat.bookchat.domain;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.VARIES;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static toy.bookchat.bookchat.domain.user.ROLE.USER;
import static toy.bookchat.bookchat.security.oauth.OAuth2Provider.GOOGLE;

import io.jsonwebtoken.Jwts;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.FieldDescriptor;
import toy.bookchat.bookchat.domain.user.ReadingTaste;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenManager;
import toy.bookchat.bookchat.security.token.openid.IdTokenManager;
import toy.bookchat.bookchat.security.token.openid.kakao.KakaoPublicKeyFetcher;
import toy.bookchat.bookchat.security.user.TokenPayload;
import toy.bookchat.bookchat.security.user.UserPrincipal;

/*
    controller 테스트는 security까지 포함시켜 테스트하여 restdoc 문서에
    token 정보가 포함되도록 진행
 */
public abstract class ControllerTestExtension {

    @MockBean
    JwtTokenManager jwtTokenManager;
    @MockBean
    IdTokenManager idTokenManager;
    @MockBean
    KakaoPublicKeyFetcher kakaoPublickeyFetcher;

    private User testUser = User.builder()
        .id(1L)
        .email("test@gmail.com")
        .nickname("testUserNickname")
        .role(USER)
        .name("testUser")
        .profileImageUrl("somethingImageUrl@naver.com")
        .defaultProfileImageType(1)
        .provider(OAuth2Provider.KAKAO)
        .readingTastes(List.of(ReadingTaste.DEVELOPMENT, ReadingTaste.ART))
        .build();

    protected User getUser() {
        return this.testUser;
    }

    protected Long getUserId() {
        return this.testUser.getId();
    }

    protected String getUserName() {
        return this.testUser.getName();
    }

    protected String getUserNickname() {
        return this.testUser.getNickname();
    }

    protected String getUserProfileImageUrl() {
        return this.testUser.getProfileImageUrl();
    }

    protected Integer getUserDefaultProfileImageType() {
        return this.testUser.getDefaultProfileImageType();
    }

    private TokenPayload getTokenPayload(User user) {
        return TokenPayload.of(user.getId(), user.getName(),
            user.getNickname(),
            user.getEmail(), user.getProfileImageUrl(), user.getDefaultProfileImageType(),
            user.getRole());
    }

    @BeforeEach
    public void setUp() {
        doReturn(getTokenPayload(getUser())).when(jwtTokenManager).getTokenPayloadFromToken(any());
    }

    public KakaoPublicKeyFetcher getPublicKeyFetcher() {
        return this.kakaoPublickeyFetcher;
    }

    public IdTokenManager getIdTokenManager() {
        return this.idTokenManager;
    }

    public JwtTokenManager getJwtTokenManager() {
        return this.jwtTokenManager;
    }

    public List<FieldDescriptor> getPageField() {
        return List.of(fieldWithPath("pageMeta.totalElements").type(NUMBER).description("전체 ROW 수"),
            fieldWithPath("pageMeta.totalPages").type(NUMBER).description("총 페이지 수"),
            fieldWithPath("pageMeta.pageSize").type(NUMBER).description("요청한 페이지 사이즈"),
            fieldWithPath("pageMeta.pageNumber").type(NUMBER).description("현재 페이지 번호"),
            fieldWithPath("pageMeta.offset").type(NUMBER).description("ROW 시작 번호"),
            fieldWithPath("pageMeta.first").type(BOOLEAN).description("시작 페이지 여부"),
            fieldWithPath("pageMeta.last").type(BOOLEAN).description("마지막 페이지 여부"),
            fieldWithPath("pageMeta.empty").type(BOOLEAN).description("content 비어있는지 여부"));
    }

    public List<FieldDescriptor> getSliceField() {
        return List.of(fieldWithPath("sliceMeta.sliceSize").type(NUMBER).description("현재 슬라이스 크기"),
            fieldWithPath("sliceMeta.sliceNumber").type(NUMBER).description("현재 슬라이스 번호"),
            fieldWithPath("sliceMeta.contentSize").type(NUMBER).description("현재 슬라이스에 담긴 내용물 크기"),
            fieldWithPath("sliceMeta.hasContent").type(BOOLEAN).description("현재 슬라이스 내용물 유/무"),
            fieldWithPath("sliceMeta.hasNext").type(BOOLEAN).description("다음 슬라이스 유무"),
            fieldWithPath("sliceMeta.hasPrevious").type(BOOLEAN).description("이전 슬라이스 유무"),
            fieldWithPath("sliceMeta.last").type(BOOLEAN).description("마지막 슬라이스 여부"),
            fieldWithPath("sliceMeta.first").type(BOOLEAN).description("처음 슬라이스 여부"));
    }

    public List<FieldDescriptor> getCursorField() {
        return List.of(fieldWithPath("cursorMeta.sliceSize").type(NUMBER).description("현재 슬라이스 크기"),
            fieldWithPath("cursorMeta.contentSize").type(NUMBER).description("현재 슬라이스에 담긴 내용물 크기"),
            fieldWithPath("cursorMeta.hasContent").type(BOOLEAN).description("현재 슬라이스 내용물 유/무"),
            fieldWithPath("cursorMeta.hasNext").type(BOOLEAN).description("다음 슬라이스 유무"),
            fieldWithPath("cursorMeta.last").type(BOOLEAN).description("마지막 슬라이스 여부"),
            fieldWithPath("cursorMeta.first").type(BOOLEAN).description("처음 슬라이스 여부"),
            fieldWithPath("cursorMeta.nextCursorId").type(VARIES).description("다음 커서 ID"));
    }

    protected String getTestToken() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "test");
        claims.put("name", "google123");
        claims.put("provider", GOOGLE);
        claims.put("email", "test@gmail.com");

        return "Bearer " + Jwts.builder()
            .setClaims(claims)
            .signWith(HS256, "test")
            .compact();
    }

    protected UserPrincipal getUserPrincipal() {
        User user = getUser();
        TokenPayload tokenPayload = TokenPayload.of(user.getId(), user.getName(),
            user.getNickname(),
            user.getEmail(), user.getProfileImageUrl(), user.getDefaultProfileImageType(),
            user.getRole());
        return UserPrincipal.create(tokenPayload);
    }
}