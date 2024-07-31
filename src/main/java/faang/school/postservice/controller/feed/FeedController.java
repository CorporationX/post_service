package faang.school.postservice.controller.feed;

import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/feed")
public class FeedController {
    private final FeedService feedService;

    @GetMapping()
    public List<FeedDto> getFeed(@RequestParam(value = "afterPostId", required = false) String afterPostId) {
        return feedService.getFeed(afterPostId);
    }

    @PostMapping("/heat")
    public ResponseEntity<String> heatCache() {
        feedService.heatCache();
        return ResponseEntity.ok("Cache heating started");
    }
}
