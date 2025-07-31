package faang.school.postservice.controller.feed;

import faang.school.postservice.dto.feed.NewsFeedRequestDto;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/v1/feed")
@RequiredArgsConstructor
@Controller
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    public void getFeed(NewsFeedRequestDto dto) {

    }

    // По хорошему ведь вынести это в PostConstruct что-бы при запуске приложения он запускался автоматом
    @GetMapping("/heat")
    public void heatFeed() {
        feedService.initializeFeedHeat();
    }
}
