package faang.school.postservice.controller.feed;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.CachedPostDto;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FeedController {
    private final RedisFeedRepository redisFeedRepository;
    private final UserContext userContext;


    @GetMapping("/feed")
    public List<CachedPostDto> getFeed(@RequestParam(required = false) Long postId) {
        Long currentUserId = userContext.getUserId();
        return redisFeedRepository.getFeed(currentUserId, postId);
    }

    @GetMapping("/heat")
    public void heatFeed() {
        redisFeedRepository.heat();
    }

}
