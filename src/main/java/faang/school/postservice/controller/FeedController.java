package faang.school.postservice.controller;

import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.service.post.FeedService;
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

    @GetMapping()
    public List<FeedDto> getFeed(@RequestParam(value = "pivotPostId", required = false) String lastPostId) {
        return feedService.getFeed(lastPostId);
    }

    @PostMapping("/heat")
    public ResponseEntity<String> heatCache() {
        feedService.heatCache();
        return ResponseEntity.ok("Cache heating started");
    }

}
