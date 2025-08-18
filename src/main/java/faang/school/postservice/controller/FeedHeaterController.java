package faang.school.postservice.controller;

import faang.school.postservice.service.feed.FeedHeaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FeedHeaterController {

    private final FeedHeaterService feedHeaterService;

    @PostMapping("/heat")
    public ResponseEntity<String> heat() {
        feedHeaterService.heatAsync();
        return ResponseEntity.accepted().body("Feed warmup started");
    }
}
