package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final UserContext userContext;

    public Post createDraftPost(Post post) {

        Long userId = userContext.getUserId();
        existsUserById(userId);
        post.setAuthorId(userId);
        post.setDeleted(false);
        post.setPublished(false);
        postRepository.save(post);
        log.info("the post was published {}", post.getId());
        return post;
    }

    public Post publishedPost(Long postId) {
        Post post = getPostWithAuthorshipValidation(postId);
        PostValidator.validatePostIsNotPublished(post);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        postRepository.save(post);
        log.info("The post {} has been published", post.getId());
        return post;
    }

    public Post updatePost(Long postId, PostUpdateDto postUpdateDto) {
        Post post = getPostWithAuthorshipValidation(postId);

        post.setContent(postUpdateDto.content());
        postRepository.save(post);
        log.info("post {} has been updated", postId);
        return post;
    }

    public void deleteById(Long postId) {
        Post post = getPostWithAuthorshipValidation(postId);

        PostValidator.ensurePostIsNotDeleted(post);
        post.setDeleted(true);
        postRepository.save(post);
        log.info("The post {} was soft deleted.", postId);
    }

    public Post getById(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new DataValidationException(String.format("This post %d does not exist.", postId)));
        log.info("Was found post by id {}", postId);
        return post;
    }

    public List<Post> getDraftPostByAuthorId(Long authorId) {
        List<Post> posts = postRepository.findPostToDraftByAuthorId(authorId);
        if (posts.isEmpty()) {
            return posts;
        }

        return posts.stream()
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .toList();
    }

    public List<Post> getPublishedPostByAuthorId(Long authorId) {
        List<Post> posts = postRepository.findPostToPublishedByAuthorId(authorId);
        if (posts.isEmpty()) {
            return posts;
        }

        return posts.stream()
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .toList();
    }

    private Post getPostWithAuthorshipValidation(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new DataValidationException(String.format("This post %d does not exist.", postId)));

        Long userId = userContext.getUserId();
        PostValidator.validateUserIsPostAuthor(userId, post.getAuthorId());

        return post;
    }

    private void existsUserById(Long userId) {
        userServiceClient.getUser(userId);
    }
}
