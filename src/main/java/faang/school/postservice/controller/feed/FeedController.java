package faang.school.postservice.controller.feed;

import faang.school.postservice.dto.post.PostOutputDto;
import faang.school.postservice.service.FeedService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${entity.feed.api_version}/feed")
public class FeedController {
    private final FeedService feedService;

    @GetMapping
    public List<PostOutputDto> getFeed(@Nullable @RequestParam(defaultValue = "0") Integer offset) {
        return feedService.getFeed(offset);
    }
}
