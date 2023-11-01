package faang.school.postservice.controller;

import faang.school.postservice.dto.FeedDto;
import faang.school.postservice.service.FeedService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/feed")
@RequiredArgsConstructor
@Slf4j
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    public FeedDto getFeedBy(@RequestParam(value = "postId", required = false) @Min(value = 1) Long postId) {
        log.info("Received request to retrieve news feed");
        return feedService.getUserFeedBy(postId);
    }
}
