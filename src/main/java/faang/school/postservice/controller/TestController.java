package faang.school.postservice.controller;

import faang.school.postservice.client.AIClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    private final AIClient aiClient;

    @GetMapping("/spellcheck")
    public String testSpellcheck(@RequestParam String text) {
        return aiClient.correctText(text);
    }
}
