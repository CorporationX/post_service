package faang.school.postservice.controller.feed;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.repository.cache.PostCacheRepository;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/feeds")
@RequiredArgsConstructor
@Slf4j
public class FeedController {

    private final PostCacheRepository postCacheRepository;
    private final FeedService feedService;

    // пока только для тестов
    @GetMapping("/{id}")
    public void getCachePost(@PathVariable long id) {
        Optional<PostDto> post = postCacheRepository.getPost(id);
        System.err.println(post.get());

        List<CommentDto> comments = postCacheRepository.getComments(id);
        System.err.println(comments);

        System.err.println(postCacheRepository.getLikes(id));
    }

    @PostMapping("/heat")
    public void feedHeater() {
        feedService.heatFeed();
        log.info("The user feed started to heat up!");
    }

}
