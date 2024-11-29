package faang.school.postservice.service.post;


import faang.school.postservice.exception.post.PostNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PostQueryService {
    private final PostRepository postRepository;
    private final PostViewCounterService postViewCounterService;

    public Post findPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
        postViewCounterService.incrementViewCount(id);
        return post;
    }
}