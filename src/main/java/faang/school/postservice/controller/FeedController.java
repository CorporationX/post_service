package faang.school.postservice.controller;

import faang.school.postservice.component.FeedHeater;
import faang.school.postservice.dto.PostResponseDto;
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
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;
    private final FeedHeater feedHeater;

    @GetMapping
    public List<PostResponseDto> getFeed(@RequestParam Long postId) {
        return feedService.getFeed(postId);
    }

    @PostMapping("/heat")
    public ResponseEntity<String> heatFeed() {
        long usersCount = feedHeater.startHeatingFeed();
        return ResponseEntity.ok("Heat feed started for " + usersCount + " users");
    }
}
