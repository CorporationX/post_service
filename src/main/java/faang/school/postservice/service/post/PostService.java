package faang.school.postservice.service.post;

import faang.school.postservice.exception.post.PostNotFoundException;
import faang.school.postservice.exception.post.PostPublishedException;
import faang.school.postservice.exception.spelling_corrector.DontRepeatableServiceException;
import faang.school.postservice.exception.spelling_corrector.RepeatableServiceException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostValidator postValidator;
    private final SpellingCorrectionService spellingCorrectionService;

    @Transactional
    public Post create(Post post) {
        postValidator.validateCreatePost(post);

        post.setPublished(false);
        post.setDeleted(false);
        post.setCreatedAt(LocalDateTime.now());

        postRepository.save(post);

        return post;
    }

    @Transactional
    public Post update(Post updatePost) {
        Post post = findPostById(updatePost.getId());

        post.setContent(updatePost.getContent());
        post.setUpdatedAt(LocalDateTime.now());

        postRepository.save(post);

        return post;
    }

    @Transactional
    public Post publish(Long id) {
        Post post = findPostById(id);

        if (post.isPublished()) {
            throw new PostPublishedException(id);
        }

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        postRepository.save(post);

        return post;
    }

    @Transactional
    public void delete(Long id) {
        Post post = findPostById(id);

        post.setDeleted(true);
        post.setUpdatedAt(LocalDateTime.now());

        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public Post findPostById(Long id) {
        return postRepository.findByIdAndNotDeleted(id).orElseThrow(() -> new PostNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Post> searchByAuthor(Post filterPost) {
        List<Post> posts = postRepository.findByAuthorId(filterPost.getAuthorId());
        posts = applyFiltersAndSorted(posts, filterPost)
                .toList();

        return posts;
    }

    @Transactional(readOnly = true)
    public List<Post> searchByProject(Post filterPost) {
        List<Post> posts = postRepository.findByProjectId(filterPost.getProjectId());
        posts = applyFiltersAndSorted(posts, filterPost)
                .toList();

        return posts;
    }

    public void correctPosts(List<Post> draftPosts) {
        draftPosts.forEach(post -> {
            try {
                String correctedContent = spellingCorrectionService.getCorrectedContent(post.getContent());
                post.setContent(correctedContent);
                post.setUpdatedAt(LocalDateTime.now());
            } catch (RepeatableServiceException exception) {
                log.error("Контент поста {} не прошёл авто корректировку, после переотправок", post.getId());
            } catch (DontRepeatableServiceException exception) {
                log.error("Контент поста {} не прошёл авто корректировку из-за ошибки сервиса", post.getId());
            }
        });

        postRepository.saveAll(draftPosts);
    }

    private Stream<Post> applyFiltersAndSorted(List<Post> posts, Post filterPost) {
        return posts.stream()
                .filter((post -> !post.isDeleted()))
                .filter((post -> post.isPublished() == filterPost.isPublished()))
                .sorted(Comparator.comparing(
                        filterPost.isPublished() ? Post::getPublishedAt : Post::getCreatedAt
                ).reversed());
    }
}
