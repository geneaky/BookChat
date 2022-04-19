package toy.bookchat.bookchat.domain;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class MainController {

    @GetMapping
    public String index() {
        return "Main Page";
    }

    @GetMapping("/auth")
    public String authenticatedUserRedirect() {
        return "you are authenticated!";
    }

}
