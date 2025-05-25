package faang.school.postservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Post Service API", description = "API for post service")
@RequestMapping("/like")
public class LikeController {

    @GetMapping("/hello")
    @Operation(summary = "Say Hello", description = "Returns a greeting message")
    public String sayHello() {
        return "Hello, Swagger!";
    }

    public void getLike() {
    }
}
