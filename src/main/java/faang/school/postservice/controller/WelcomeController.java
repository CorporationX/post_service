package faang.school.postservice.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Post service")
public class WelcomeController {

    @GetMapping("welcome")
    public String welcomeToOurApp() {
        return "Hello from post service, test your endpoints from swagger end enjoy.";
    }
}
