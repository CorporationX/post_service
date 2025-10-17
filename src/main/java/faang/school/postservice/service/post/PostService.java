package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

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

        if (post.getProjectId() != null) {
            existsProjectById(post.getProjectId());
        }

        post.setAuthorId(userId);
        post.setPostStatus(DRAFT);
        post.setDeleted(false);
        post.setPublished(false);
        postRepository.save(post);
        log.info("the post was published {}", post.getId());
        return post;
    }

    public Post publishedPost(Long postId) {
        Long userId = userContext.getUserId();
        PostValidator.validateUserIsPostAuthor(userId, postId);

        Optional<Post> optionalPost = postRepository.findById(postId);
        Post post = PostValidator.validatePostExists(optionalPost, postId);

        PostValidator.validatePostIsNotPublished(post);

        post.setPublished(true);
        post.setPostStatus(PUBLISHED);
        post.setPublishedAt(LocalDateTime.now());

        postRepository.save(post);
        log.info("The post {} has been published", post.getId());
        return post;
    }

    private void existsUserById(Long userId) {
        userServiceClient.getUser(userId);
    }

    private void existsProjectById(Long projectId) {
        projectServiceClient.getProject(projectId);
    }
}
