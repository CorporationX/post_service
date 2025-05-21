package faang.school.postservice.service.post;

import faang.school.postservice.exception.post.PostNotFoundException;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.repository.post.PostRepository;
import faang.school.postservice.validation.post.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final PostValidator postValidator;

    // TODO: проверка что пользователь или проект существуют
    @Transactional
    public Post createDraftPost(final Post post) {
        postValidator.validatePost();
        Post savedPost = postRepository.save(post);
        log.info("Post with id {} was created", savedPost.getId());
        return savedPost;
    }

    @Transactional
    public Post publishPost(final long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        postValidator.checkPostIsNotPublished(post);

        LocalDateTime now = LocalDateTime.now();
        post.setPublished(true);
        post.setPublishedAt(now);
        post = postRepository.save(post);
        log.info("Post with id {} was published {}", postId, now);

        return post;
    }
}
