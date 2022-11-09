package toy.bookchat.bookchat.domain;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static toy.bookchat.bookchat.domain.user.ROLE.USER;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.FieldDescriptor;
import toy.bookchat.bookchat.config.OpenIdTokenConfig;
import toy.bookchat.bookchat.domain.user.ReadingTaste;
import toy.bookchat.bookchat.domain.user.User;
import toy.bookchat.bookchat.security.ipblock.IpBlockManager;
import toy.bookchat.bookchat.security.oauth.OAuth2Provider;
import toy.bookchat.bookchat.security.token.jwt.JwtTokenManager;
import toy.bookchat.bookchat.security.token.openid.OpenIdTokenManager;
import toy.bookchat.bookchat.security.user.TokenPayload;

/*
    controller 테스트는 security까지 포함시켜 테스트하여 restdoc 문서에
    token 정보가 포함되도록 진행
 */
public abstract class ControllerTestExtension {

    @MockBean
    OpenIdTokenManager openIdTokenManager;
    @MockBean
    JwtTokenManager jwtTokenManager;
    @MockBean
    OpenIdTokenConfig openIdTokenConfig;
    @MockBean
    IpBlockManager ipBlockManager;

    private User getUser() {
        return User.builder()
            .id(1L)
            .email("test@gmail.com")
            .nickname("nickname")
            .role(USER)
            .name("testUser")
            .profileImageUrl("somethingImageUrl@naver.com")
            .defaultProfileImageType(1)
            .provider(OAuth2Provider.KAKAO)
            .readingTastes(List.of(ReadingTaste.DEVELOPMENT, ReadingTaste.ART))
            .build();
    }

    private TokenPayload getTokenPayload(User user) {
        return TokenPayload.of(user.getId(), user.getName(),
            user.getNickname(),
            user.getEmail(), user.getProfileImageUrl(), user.getDefaultProfileImageType(),
            user.getRole());
    }

    @BeforeEach
    public void setUp() {
        when(jwtTokenManager.getTokenPayloadFromToken(any())).thenReturn(
            getTokenPayload(getUser()));
        when(ipBlockManager.validateRequest(any())).thenReturn(true);
    }

    public OpenIdTokenConfig getOpenIdTokenConfig() {
        return this.openIdTokenConfig;
    }

    public OpenIdTokenManager getOpenIdTokenManager() {
        return this.openIdTokenManager;
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
            fieldWithPath("cursorMeta.hasPrevious").type(BOOLEAN).description("이전 슬라이스 유무"),
            fieldWithPath("cursorMeta.last").type(BOOLEAN).description("마지막 슬라이스 여부"),
            fieldWithPath("cursorMeta.first").type(BOOLEAN).description("처음 슬라이스 여부"));
    }
}
