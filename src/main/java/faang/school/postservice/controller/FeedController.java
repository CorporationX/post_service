package faang.school.postservice.controller;

import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.collection.SynchronizedCollection;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/feed")
public class FeedController {
    private final FeedService feedService;

    @GetMapping
    public List<FeedDto> getFeed(Long postId) {
        log.info("Received to get feed by post id: {}", postId);
        return feedService.getFeed(postId);
    }

    @PostMapping("/start")
    public void startFeed() {
        log.info("Received to start feed for users");
        feedService.heatFeed();
    }
}
