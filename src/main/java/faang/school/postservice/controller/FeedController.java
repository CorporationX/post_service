package faang.school.postservice.controller;

import faang.school.postservice.dto.FeedDto;
import faang.school.postservice.service.FeedService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/feed")
public class FeedController {

    private final FeedService feedService;

    @GetMapping()
    public Page<FeedDto> getFeed(@RequestParam (value = "postId", required = false)
                                     @Min(value = 1) Long postId) {
        return feedService.getFeed(postId);

    }
}
