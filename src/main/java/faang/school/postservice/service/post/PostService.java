package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static faang.school.postservice.model.PostStatus.DELETED;
import static faang.school.postservice.model.PostStatus.DRAFT;
import static faang.school.postservice.model.PostStatus.PUBLISHED;

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
        post.setPostStatus(DRAFT);
        post.setDeleted(false);
        post.setPublished(false);
        postRepository.save(post);
        log.info("the post was published {}", post.getId());
        return post;
    }

    public Post publishedPost(Long postId) {
        Post post = checkUserContextAndGetPostById(postId);

        PostValidator.validatePostIsNotPublished(post);

        post.setPublished(true);
        post.setPostStatus(PUBLISHED);
        post.setPublishedAt(LocalDateTime.now());

        postRepository.save(post);
        log.info("The post {} has been published", post.getId());
        return post;
    }

    public Post updatePost(Long postId, PostUpdateDto postUpdateDto) {
        Post post = checkUserContextAndGetPostById(postId);

        post.setContent(postUpdateDto.content());
        postRepository.save(post);
        log.info("post {} has been updated", postId);
        return post;
    }

    public void deleteById(Long postId) {
        Post post = checkUserContextAndGetPostById(postId);

        PostValidator.validatePostIsDeleted(post);

        post.setPostStatus(DELETED);
        post.setDeleted(true);
        postRepository.save(post);
        log.info("The post {} was soft deleted.", postId);
    }

    public Post getById(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        Post post = PostValidator.validatePostExists(optionalPost, postId);
        log.info("Was found post by id {}", postId);
        return post;
    }

    public List<Post> getDraftPostByAuthorId(Long authorId) {
        List<Post> posts = postRepository.findByAuthorId(authorId);
        if (posts.isEmpty()) {
            return posts;
        }

        return posts.stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .toList();
    }

    public List<Post> getPublishedPostByAuthorId(Long authorId) {
        List<Post> posts = postRepository.findByAuthorId(authorId);
        if (posts.isEmpty()) {
            return posts;
        }

        return posts.stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .toList();
    }

    private Post checkUserContextAndGetPostById(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        Post post = PostValidator.validatePostExists(optionalPost, postId);

        Long userId = userContext.getUserId();
        PostValidator.validateUserIsPostAuthor(userId, post.getAuthorId());

        return post;
    }

    private void existsUserById(Long userId) {
        userServiceClient.getUser(userId);
    }

}
