package faang.school.postservice.controller.feed;

import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.dto.feed.NewsFeedRequestDto;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("${news-feed.api-version}${news-feed.api-endpoint}")
@RequiredArgsConstructor
@RestController
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    public FeedDto getFeed(NewsFeedRequestDto dto) {
        return feedService.getFeed(dto.postId());
    }

    @GetMapping("/heat")
    public void heatFeed() {
        feedService.initializeFeedHeat();
    }
}