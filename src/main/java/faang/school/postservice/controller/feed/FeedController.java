package faang.school.postservice.controller.feed;

import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("${news-feed.api-version}${news-feed.api-endpoint}")
@RequiredArgsConstructor
@RestController
@Slf4j
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    public FeedDto getFeed(@RequestParam(required = false) Long postId) {
        log.info("dto: {}", postId);
        return feedService.getFeed(postId);
    }

    @GetMapping("/heat")
    public void heatFeed() {
        feedService.initializeFeedHeat();
    }
}