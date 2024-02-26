package toy.bookchat.bookchat.domain.device.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import toy.bookchat.bookchat.domain.ControllerTestExtension;
import toy.bookchat.bookchat.domain.device.controller.dto.request.UpdateFcmTokenRequest;
import toy.bookchat.bookchat.domain.device.service.DeviceService;


@DevicePresentationTest
class DeviceControllerTest extends ControllerTestExtension {

    public final String JWT_TOKEN = getTestToken();
    @MockBean
    private DeviceService deviceService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void fcmToken_갱신_성공() throws Exception {
        UpdateFcmTokenRequest request = UpdateFcmTokenRequest.builder()
            .fcmToken("changed fcm token")
            .build();

        mockMvc.perform(put("/v1/api/devices/fcm-token")
                .header(AUTHORIZATION, JWT_TOKEN)
                .with(user(getUserPrincipal()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(document("update-fcm-token",
                requestHeaders(
                    headerWithName(AUTHORIZATION).description("Bearer [JWT token]")
                ),
                requestFields(
                    fieldWithPath("fcmToken").description("FCM Token")
                )
            ));
    }
}