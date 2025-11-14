package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.event.UserBanEvent;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.UserBanEventPublisher;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final static String DRAFT_LOG_PREFIX = "drafts";
    private final static String PUBLISHED_LOG_PREFIX = "published posts";

    @Value("${posts.max-unverified-posts}")
    private int maxUnverifiedPosts;

    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final UserBanEventPublisher userBanEventPublisher;

    @Override
    public PostDto createDraft(CreatePostDto postDto) {
        validatePost(postDto);

        Post post = postMapper.toPost(postDto);
        post.setPublished(false);
        post.setDeleted(false);
        post = postRepository.save(post);

        log.info("Draft created successfully with ID: {}", post.getId());
        return postMapper.toPostDto(post);
    }

    @Override
    public PostDto publishPost(Long postId) {
        Post post = findPostOrThrow(postId);
        if (post.isPublished()) {
            throw new ForbiddenException("Post is already published!");
        }
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        post = postRepository.save(post);

        log.info("Post published successfully with ID: {}", post.getId());
        return postMapper.toPostDto(post);
    }

    @Override
    public PostDto updatePost(Long postId, CreatePostDto postDto) {
        validatePost(postDto);

        Post post = findPostOrThrow(postId);
        if ((!Objects.equals(post.getAuthorId(), postDto.authorId())) ||
                (!Objects.equals(post.getProjectId(), postDto.projectId()))) {
            throw new DataValidationException("Cannot change the author or project of a post.");
        }
        post.setContent(postDto.content());
        post = postRepository.save(post);

        log.info("Post with ID {} has been updated", post.getId());
        return postMapper.toPostDto(post);
    }

    @Override
    public PostDto softDeletePost(Long postId) {
        Post post = findPostOrThrow(postId);
        post.setDeleted(true);
        post = postRepository.save(post);

        log.info("Post with ID {} has been softly deleted", post.getId());
        return postMapper.toPostDto(post);
    }

    @Override
    public PostDto getPostById(Long postId) {
        Post post = findPostOrThrow(postId);

        log.info("Post with ID {} was fetched", post.getId());
        return postMapper.toPostDto(post);
    }

    @Override
    public List<PostDto> getDraftsByUser(Long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        return getFilteredPostDto(userId, posts, false, DRAFT_LOG_PREFIX);
    }

    @Override
    public List<PostDto> getDraftsByProject(Long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        return getFilteredPostDto(projectId, posts, false, DRAFT_LOG_PREFIX);
    }

    @Override
    public List<PostDto> getPublishedByUser(Long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        return getFilteredPostDto(userId, posts, true, PUBLISHED_LOG_PREFIX);
    }

    @Override
    public List<PostDto> getPublishedByProject(Long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        return getFilteredPostDto(projectId, posts, true, PUBLISHED_LOG_PREFIX);
    }


    @Override
    public void findAuthorsForBan() {
        List<Post> unverifiedPosts = postRepository.findUnverified();
        Map<Long, Long> postsByAuthors = unverifiedPosts.stream().collect(Collectors.groupingBy(
                Post::getAuthorId,
                Collectors.counting()
        ));

        List<Long> authorsToBan = postsByAuthors.entrySet().stream()
                .filter(entry -> entry.getValue() > maxUnverifiedPosts)
                .map(Map.Entry::getKey)
                .toList();


        if (!authorsToBan.isEmpty()) {
            authorsToBan = getNotBannedUsers(authorsToBan);
        }

        if (!authorsToBan.isEmpty()) {
            userBanEventPublisher.publish(UserBanEvent.builder().userIds(authorsToBan).build());
        }
    }

    @NotNull
    private List<PostDto> getFilteredPostDto(Long ownerId, List<Post> posts,
                                             boolean published, String logPrefix) {
        if (posts.isEmpty()) {
            log.info("No posts have been fetched");
            return Collections.emptyList();
        }

        List<Post> sortedAndFilteredPosts = posts.stream()
                .filter(post -> post.isPublished() == published && !post.isDeleted())
                .sorted((post1, post2) -> {
                    var date1 = published ? post1.getPublishedAt() : post1.getCreatedAt();
                    var date2 = published ? post2.getPublishedAt() : post2.getCreatedAt();
                    return date1.compareTo(date2);
                })
                .toList();

        log.info("Fetched {} {} by owner ID {}", sortedAndFilteredPosts.size(), logPrefix, ownerId);

        return sortedAndFilteredPosts.stream()
                .map(postMapper::toPostDto)
                .toList();
    }

    private Post findPostOrThrow(Long postId) {
        return postRepository.findById(postId).orElseThrow(() ->
                new EntityNotFoundException("Post not found with id: " + postId));
    }

    private void validatePost(CreatePostDto postDto) {
        validateIdNotNegative(postDto.authorId(), "Author");
        validateIdNotNegative(postDto.projectId(), "Project");

        boolean hasAuthor = isValidId(postDto.authorId());
        boolean hasProject = isValidId(postDto.projectId());

        if ((hasAuthor && hasProject) || (!hasAuthor && !hasProject)) {
            throw new DataValidationException("Post must have either an author or a project, but not both");
        }

        validateAuthor(postDto, hasAuthor); // Через postman, всегда выкидывает исключение
        validateProject(postDto, hasProject); // Пока не доходит до исключения валидации. Причина - ???
    }

    private void validateProject(CreatePostDto postDto, boolean hasProject) {
        if (hasProject) {
            try {
                projectServiceClient.getProject(postDto.projectId());
            } catch (FeignException.NotFound e) {
                throw new EntityNotFoundException("Project with ID " + postDto.projectId() + " not found");
            }
        }
    }

    private void validateAuthor(CreatePostDto postDto, boolean hasAuthor) {
        if (hasAuthor) {
            try {
                userServiceClient.getUser(postDto.authorId());
            } catch (FeignException.NotFound e) {
                throw new EntityNotFoundException("User with ID " + postDto.authorId() + " not found");
            }
        }
    }

    private boolean isValidId(Long id) {
        return id != null && id > 0;
    }

    private void validateIdNotNegative(Long id, String entityName) {
        if (id != null && id < 0) {
            throw new DataValidationException(entityName + " ID cannot be negative");
        }
    }

    @Retryable(retryFor = {FeignException.InternalServerError.class, FeignException.ServiceUnavailable.class},
            maxAttemptsExpression = "${user-service.retryable.maxAttempts}",
            backoff = @Backoff(delayExpression = "${user-service.retryable.delay}",
                    multiplierExpression = "${user-service.retryable.multiplier}"))
    private List<Long> getNotBannedUsers(List<Long> usersIds) {
        return userServiceClient.getNotBannedUsersIds(usersIds);
    }
}
