package faang.school.postservice.controller;

import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feeds")
@RequiredArgsConstructor
@Validated
public class FeedController {
    private final FeedService feedService;

    @GetMapping("/feed")
    public List<FeedDto> getPostFeedBatch(@RequestParam(value = "postIndex") long postIndex) {
        return feedService.getFeed(postIndex);
    }
}
