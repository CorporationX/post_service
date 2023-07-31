package faang.school.postservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

    @GetMapping("welcome")
    public String welcomeToOurApp() {
        return "Hello from post service, test your endpoints from swagger end enjoy.";
    }
}
