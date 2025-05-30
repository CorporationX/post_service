package faang.school.postservice.controller.newsfeed;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.newsfeed.FeedService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feed")
@Validated
public class FeedController {

    private final FeedService feedService;

    @GetMapping("/heat")
    public ResponseEntity<List<PostDto>> getFeed(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size
    ) {
            List<PostDto> feed = feedService.getFeed(page, size);
            return ResponseEntity.ok(feed);
    }
}
