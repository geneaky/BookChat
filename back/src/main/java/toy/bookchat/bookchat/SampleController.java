package toy.bookchat.bookchat;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
public class SampleController {

    @GetMapping
    public String index() {
        return "hello";
    }

    @GetMapping("/user/{id}")
    public UserId userId(@PathVariable String id) {
        UserId contact = UserId.builder()
                .contact(new HashMap<>())
                .contact2(new HashMap<>())
                .build();
        contact.getContact().put("name", "jane doe" + id);
        contact.getContact().put("email", "jane@gmail.com");
        contact.getContact2().put("name", "jane" + id);
        contact.getContact2().put("email", "jane@naver.com");

        return contact;
    }

    @GetMapping("/users")
    public String parameter(@RequestParam Integer page, @RequestParam(required = false) Integer per_page) {
        return "book : " + page + per_page;
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public String paramter2(@RequestParam String username) {
        return "create-user-name is : " + username;
    }

    @Builder
    @Getter
    @Setter
    public static class UserId {
        HashMap<String, String> contact;
        HashMap<String, String> contact2;
    }

    @GetMapping("/books/{id}")
    public List<HashMap<String, String>> book(@PathVariable String id) {
        List<HashMap<String, String>> response = new ArrayList<>();
        HashMap<String, String> temp1 = new HashMap<>();
        temp1.put("title", "pride and prejudice");
        temp1.put("author", "jane");
        HashMap<String, String> temp2 = new HashMap<>();
        temp2.put("title", "to kill a mockingbird");
        temp2.put("author", "harper lee");
        response.add(temp1);
        response.add(temp2);

        return response;
    }

    @GetMapping("/locations/{id}")
    public HashMap<String, HashMap<String, HashMap<String, Double>>> location(@PathVariable String id) {
        HashMap<String, Double> wind = new HashMap<>();
        wind.put("speed", 15.3);
        wind.put("direction", 287.0);

        HashMap<String, Double> temperature = new HashMap<>();
        temperature.put("high", 15.3);
        temperature.put("low", 287.0);

        HashMap<String, HashMap<String, Double>> weather = new HashMap<>();
        weather.put("wind", wind);
        weather.put("temperature", temperature);

        HashMap<String, HashMap<String, HashMap<String, Double>>> response = new HashMap<>();
        response.put("weather", weather);

        return response;
    }

    @GetMapping("/locations/{latitude}/{longitude}")
    public String locationPath(@PathVariable Double latitude, @PathVariable Double longitude) {
        return "location : " + latitude + longitude;
    }

    @PostMapping("/upload")
    public String reqeustPart(@RequestPart MultipartFile file) {

        return file.getOriginalFilename();
    }

    @PostMapping("/images")
    public String requestPartBody(@RequestPart MultipartFile image) {

        return image.getOriginalFilename();
    }

    @GetMapping(value = "/people")
    public String requestHeader(@RequestHeader("Authorization") String headers, HttpServletResponse response) {
        response.setHeader("X-RateLimit-Limit","a");
        response.setHeader("X-RateLimit-Remaining","b");
        response.setHeader("X-RateLimit-Reset","c");

        return headers;
    }
}
