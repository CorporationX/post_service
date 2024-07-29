package faang.school.postservice.controller.feed;

import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/feeds")
public class FeedController {
    private final FeedService feedService;

    @GetMapping("/feed")
    public List<FeedDto> getFeed(@RequestParam(value = "afterPostId", required = false) String afterPostId) {
        return feedService.getFeed(afterPostId);
    }
}
