package toy.bookchat.bookchat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.headers.HeaderDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import toy.bookchat.bookchat.domain.chat.RestDocExtention;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ExtendWith(RestDocumentationExtension.class)
class SampleControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation).operationPreprocessors()
                        .withResponseDefaults(prettyPrint()))
//                .alwaysDo(document("{method-name}/{step}"))
                .build();
    }



    @Test
    @DisplayName("detail exact payload field")
    public void user_id_get_test() throws Exception {
        //given
        mockMvc.perform(get("/user/5"))
                .andExpect(status().isOk())
                .andDo(document("index", responseFields(
                        fieldWithPath("contact.email").type(JsonFieldType.STRING).description("The user's email address"),
                        fieldWithPath("contact.name").description("The user's name"),
                        fieldWithPath("contact2.name").description("test user name"),
                        fieldWithPath("contact2.email").description("test user email")
                )));

        //when

        //then

    }

    @Test
    @DisplayName("entire subsection of payload not detail field - subsection")
    public void user_id_get_test2() throws Exception {
        //given
        mockMvc.perform(get("/user/5"))
                .andExpect(status().isOk())
                .andDo(document("index", responseFields(
                        subsectionWithPath("contact").description("The user's contact details"),
                        subsectionWithPath("contact2").description("the user's contact test")
                )));

        //when

        //then

    }

    @Test
    @DisplayName("document field in a relaxed mode, where any undocumented fields do not cause a test failure")
    public void user_id_get_test3() throws Exception {
        //given
        mockMvc.perform(get("/user/5"))
                .andExpect(status().isOk())
                .andDo(document("index", relaxedResponseFields(
                        fieldWithPath("contact.name").description("relax mode name")
                )));

        //when

        //then

    }

    @Test
    @DisplayName("reuse same structure by descriptor")
    public void book_descriptor_api_test() throws Exception {
        //single book
        FieldDescriptor[] singleBook = new FieldDescriptor[]{
                fieldWithPath("title").description("title of book"),
                fieldWithPath("author").description("author of book")
        };

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andDo(document("book", responseFields(fieldWithPath("[]")
                        .description("array of books"))
                        .andWithPrefix("[].",singleBook)));

        //when

        //then

    }


    @Test
    @DisplayName("docuemting subsection of payload structurally complex")
    public void documenting_subsection_of_resposne() throws Exception {
        //given
        mockMvc.perform(get("/locations/1"))
                .andExpect(status().isOk())
                .andDo(document("location", responseBody(
                        beneathPath("weather")
                                .withSubsectionId("weather")
                )));

        //when

        //then

    }

    @Test
    @DisplayName("document fields in a particular subsection")
    public void docuementing_subsection_of_response2() throws Exception {
        //given
        mockMvc.perform(get("/locations/1"))
                .andExpect(status().isOk())
                .andDo(document("location", responseFields(beneathPath("weather.temperature"),
                        fieldWithPath("high").description("the forecast high in degrees"),
                        fieldWithPath("low").description("the forecast low in degrees"))));

        //when

        //then

    }

    @Test
    @DisplayName("document request parameter by using requestParameters")
    public void reqeuset_parameter() throws Exception {
        //given
        mockMvc.perform(get("/users?page=2&per_page=100"))
                .andExpect(status().isOk())
                .andDo(document("users", requestParameters(
                        parameterWithName("page").description("The page to retrieve"),
                        parameterWithName("per_page").description("Entries per page")
                )));

        //when

        //then

    }

    @Test
    @DisplayName("document request param by form data")
    public void request_parameter_form() throws Exception {
        //given
        mockMvc.perform(post("/users").param("username", "Tester"))
                .andExpect(status().isCreated())
                .andDo(document("create-user", requestParameters(
                        parameterWithName("username").description("the user's username")
                )));

        //when

        //then

    }

    @Test
    @DisplayName("use relaxmode where any documented parameters do not cause test failure")
    public void request_parameter_relax_mode() throws Exception {
        //given
        mockMvc.perform(get("/users?page=10"))
                .andExpect(status().isOk())
                .andDo(document("users", relaxedRequestParameters(
                        parameterWithName("page").description("book page")
                )));

        //when

        //then

    }

    @Test
    @DisplayName("document path variable test")
    public void path_parameter_test() throws Exception {
        //given
        mockMvc.perform(get("/locations/{latitude}/{longitude}", 51.5072, 0.1275))
                .andExpect(status().isOk())
                .andDo(document("locations", pathParameters(
                        parameterWithName("latitude").description("the location's latitude"),
                        parameterWithName("longitude").description("the location's longitude")
                )));

        //when

        //then

    }

    @Test
    public void multi_part_test() throws Exception {
        //given
        mockMvc.perform(multipart("/upload").file("file", "example".getBytes(StandardCharsets.UTF_8)))
                .andExpect(status().isOk())
                .andDo(document("upload", requestParts(
                        partWithName("file").description("the file to upload")
                )));

        //when

        //then

    }

    @Test
    public void request_part_payload_test() throws Exception {
        //given
        MockMultipartFile image = new MockMultipartFile("image", "image.png", "image/png", "<<pngdata>>".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile metadata = new MockMultipartFile("metadata", "", "application/json", "{ \"version\": \"1.0\"}".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/images").file(image).file(metadata))
                .andExpect(status().isOk())
                .andDo(document("image-upload", requestPartBody("image")));

        //when

        //then

    }

    @Test
    public void request_part_fields() throws Exception {
        //given
        MockMultipartFile image = new MockMultipartFile("image", "image.png", "image/png", "<<pngdata>>".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile metadata = new MockMultipartFile("metadata", "", "application/json", "{ \"version\": \"1.0\"}".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/images").file(image).file(metadata))
                .andExpect(status().isOk())
                .andDo(document("image-upload", requestPartFields("metadata",
                        fieldWithPath("version").description("the version of the images")
                )));

        //when

        //then

    }
    
    @Test
    public void request_header_test() throws Exception {
        //given
        mockMvc.perform(get("/people").header("Authorization", "Basic dXNlcjpzZWNyZXQ="))
                .andExpect(status().isOk())
                .andDo(document("headers", requestHeaders(
                                headerWithName("Authorization").description("Basic auth credentials")),
                        responseHeaders(
                                headerWithName("X-RateLimit-Limit").description("the total number of requests permitted per period").attributes(key("constraints").value("must have")),
                                headerWithName("X-RateLimit-Remaining").description("Remaining requests permitted in current period"),
                                headerWithName("X-RateLimit-Reset").description("Time at which the rate limit period will reset")
                        )
                ));
        
        //when
        
        //then
        
    }

    public void example() {
        ConstraintDescriptions userConstraints = new ConstraintDescriptions(UserInput.class);

        List<String> descriptions = userConstraints.descriptionsForProperty("name");
    }


    static class UserInput {
        @NotNull
        @Size(min = 1)
        String name;

        @NotNull
        @Size(min = 8)
        String password;
    }

    @Test
    public void customizing_request_response() throws Exception {
        //given
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andDo(document("index", preprocessRequest(removeHeaders("Foo")),
                        preprocessResponse(prettyPrint())));

        //when

        //then

    }


}