package faang.school.postservice.controller.feed;

import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.service.feed.FeedHeater;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/v1/feed")
@RequiredArgsConstructor
public class FeedController {
    private final FeedService service;
    private final FeedHeater heater;

    @GetMapping
    public List<FeedDto> feed(@RequestParam(required = false) Long lastPostId,
                              @RequestParam(required = false, defaultValue = "20") Integer limit) {
        return service.getFeed(lastPostId, limit);
    }

    @PutMapping("/heat")
    public void heat() {
        heater.start();
    }
}
