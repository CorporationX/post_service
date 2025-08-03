package faang.school.postservice.controller.feed;

import faang.school.postservice.dto.post.PostOutputDto;
import faang.school.postservice.service.FeedHeater;
import faang.school.postservice.service.FeedService;
import jakarta.annotation.Nullable;
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
@RequestMapping("${entity.feed.api_version}/feed")
public class FeedController {
    private final FeedService feedService;
    private final FeedHeater feedHeater;

    @GetMapping
    public List<PostOutputDto> getFeed(@Nullable @RequestParam(defaultValue = "0") Integer offset) {
        return feedService.getFeed(offset);
    }

    @GetMapping("/heat")
    public void heat() {
        try {
            log.info("Feed heating started");

            feedHeater.heatAll();

            log.info("Feed heating completed");
        } catch (Exception e) {
            log.error("Unexpected exception on feed heating: {}", e.getMessage(), e);
        }
    }
}
