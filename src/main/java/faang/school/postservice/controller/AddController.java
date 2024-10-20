package faang.school.postservice.controller;

import faang.school.postservice.publisher.AdBoughtEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ads")
@RequiredArgsConstructor
public class AddController {

    private final AdBoughtEventPublisher adBoughtEventPublisher;

    @PostMapping("/publish")
    public ResponseEntity<String> publishAdBoughtEvent(@RequestBody String message) {
        adBoughtEventPublisher.publish(message);
        return ResponseEntity.ok("Ad bought event published successfully");
    }
}
