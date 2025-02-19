package faang.school.postservice.controller;

import faang.school.postservice.util.ModerationDictionaryUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/moderation")
public class ModerationController {

    private final ModerationDictionaryUtil moderationDictionaryUtil;

    @PostMapping("/check")
    public ResponseEntity<String> checkContent(@RequestBody String content) {
        if (moderationDictionaryUtil.containsBannedWords(content)) {
            return ResponseEntity.ok("The content contains banned words.");
        }
        return ResponseEntity.ok("The content is clean.");
    }
}