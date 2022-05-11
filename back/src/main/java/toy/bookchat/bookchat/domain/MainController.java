package toy.bookchat.bookchat.domain;


import javax.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class MainController {

    @GetMapping
    public String index() {
        return "Main Page";
    }

    @GetMapping(value = "/auth", produces = MediaType.TEXT_HTML_VALUE)
    public String authenticatedUserRedirect(@RequestParam String token,
        HttpServletResponse response) {
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        return "<html>\n" + "<header><title>BookChat</title>"
            + "</header>\n" +
            "<body>\n" + "<p> you are authenticated! </p>\n"
            + "<a href=\"http://localhost:8080/app?token=" + token
            + "\" style=\"font: bold 11px Arial;\n"
            + "  text-decoration: none;\n"
            + "  background-color: #EEEEEE;\n"
            + "  color: #333333;\n"
            + "  padding: 2px 6px 2px 6px;\n"
            + "  border-top: 1px solid #CCCCCC;\n"
            + "  border-right: 1px solid #333333;\n"
            + "  border-bottom: 1px solid #333333;\n"
            + "  border-left: 1px solid #CCCCCC; \"> back to BookChat </a>"
            + "</body>\n" + "</html>";
    }

    @GetMapping("/app")
    public String comebackToApp(@RequestParam String token, HttpServletResponse response) {
        response.setHeader("token", token);
        return "have a good time";
    }

}
