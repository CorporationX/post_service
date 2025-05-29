package faang.school.postservice.service.post;

import faang.school.postservice.exception.post.PostNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public Post getById(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.error("Post with id {} not found", postId);
                    return new PostNotFoundException(postId);
                });
    }
}
