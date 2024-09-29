package faang.school.postservice.controller.feed;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.repository.cache.PostCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class FeedController {

    private final PostCacheRepository postCacheRepository;
    // пока только для тестов
    @GetMapping("/feed/{id}")
    public void getCachePost(@PathVariable long id) {
        Optional<PostDto> post = postCacheRepository.getPost(id);
        System.err.println(post.get());

        List<CommentDto> comments = postCacheRepository.getComments(id);
        System.err.println(comments);

        System.err.println(postCacheRepository.getLikes(id));
    }

}
