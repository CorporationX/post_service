package faang.school.postservice.controller;

import faang.school.postservice.dto.PostFeedResponseDto;
import faang.school.postservice.service.FeedHeater;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feed")
public class FeedController {
    private final FeedService service;
    private final FeedHeater feedHeater;

    @GetMapping
    public ResponseEntity<List<PostFeedResponseDto>> getFeed(@RequestParam(required = false) Long afterPostId) {
        List<PostFeedResponseDto> feed = service.getFeed(afterPostId);
        return ResponseEntity.ok(feed);
    }

    @PostMapping("/heat")
    public ResponseEntity<String> heatFeedCache() throws InterruptedException {
        feedHeater.heatFeedCache();
        return ResponseEntity.ok("Начинаю прогрев кеша фида");
    }
}
