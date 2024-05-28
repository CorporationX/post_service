package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.post.PostOperationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static faang.school.postservice.exception.post.PostOperationExceptionMessage.DELETED_STATUS_UPDATE_EXCEPTION;
import static faang.school.postservice.exception.post.PostOperationExceptionMessage.LIKES_UPDATE_EXCEPTION;
import static faang.school.postservice.exception.post.PostOperationExceptionMessage.PUBLISHED_DATE_UPDATE_EXCEPTION;
import static faang.school.postservice.exception.post.PostOperationExceptionMessage.RE_DELETING_POST_EXCEPTION;
import static faang.school.postservice.exception.post.PostOperationExceptionMessage.RE_PUBLISHING_POST_EXCEPTION;
import static faang.school.postservice.exception.post.PostValidationExceptionMessage.NON_EXISTING_POST_EXCEPTION;
import static faang.school.postservice.exception.post.PostValidationExceptionMessage.NON_EXISTING_PROJECT_EXCEPTION;
import static faang.school.postservice.exception.post.PostValidationExceptionMessage.NON_EXISTING_USER_EXCEPTION;
import static faang.school.postservice.exception.post.PostValidationExceptionMessage.NON_MATCHING_AUTHORS_EXCEPTION;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    public PostDto createPost(@Valid PostDto postDto) {
        checkAuthorExistence(postDto.getAuthorId(), postDto.getProjectId());

        Post postDraft = postMapper.toEntity(postDto);
        postDraft.setPublished(false);
        postDraft.setDeleted(false);

        return postMapper.toDto(postRepository.save(postDraft));
    }

    public PostDto publishPost(long postId) {
        Post postToBePublished = getPost(postId);

        if (postToBePublished.isPublished()) {
            throw new PostOperationException(RE_PUBLISHING_POST_EXCEPTION.getMessage());
        }

        postToBePublished.setPublished(true);
        postToBePublished.setPublishedAt(LocalDateTime.now());

        return postMapper.toDto(postRepository.save(postToBePublished));
    }

    public PostDto updatePost(PostDto postDto) {
        checkAuthorExistence(postDto.getAuthorId(), postDto.getProjectId());

        Post postToBeUpdated = getPost(postDto.getId());

        checkPostMatchingWithSystem(postDto, postToBeUpdated);

        postToBeUpdated.setContent(postDto.getContent());
        return postMapper.toDto(postRepository.save(postToBeUpdated));
    }

    public void deletePost(long postId) {
        Post postToBeDeleted = getPost(postId);

        if (postToBeDeleted.isDeleted()) {
            throw new DataValidationException(RE_DELETING_POST_EXCEPTION.getMessage());
        }

        postToBeDeleted.setDeleted(true);

        postMapper.toDto(postRepository.save(postToBeDeleted));
    }

    public PostDto getPostById(long postId) {
        return postMapper.toDto(getPost(postId));
    }

    public List<PostDto> getDraftsOfUser(long userId) {
        checkUserExistence(userId);

        return getSortedDrafts(postRepository.findByAuthorId(userId));
    }

    public List<PostDto> getDraftsOfProject(long projectId) {
        checkProjectExistence(projectId);

        return getSortedDrafts(postRepository.findByProjectId(projectId));
    }

    public List<PostDto> getPostsOfUser(long userId) {
        checkUserExistence(userId);

        return getSortedPosts(postRepository.findByAuthorId(userId));
    }

    public List<PostDto> getPostsOfProject(long projectId) {
        checkProjectExistence(projectId);

        return getSortedPosts(postRepository.findByProjectId(projectId));
    }

    private void checkUserExistence(long userId) {
        if (!userServiceClient.existsById(userId)) {
            throw new DataValidationException(NON_EXISTING_USER_EXCEPTION.getMessage());
        }
    }

    private void checkProjectExistence(long projectId) {
        if (!projectServiceClient.existsById(projectId)) {
            throw new DataValidationException(NON_EXISTING_PROJECT_EXCEPTION.getMessage());
        }
    }

    private void checkPostMatchingWithSystem(PostDto postDto, Post postToBeUpdated) {
        Long userAuthorIdFromDto = postDto.getAuthorId();
        if (userAuthorIdFromDto != null && !Objects.equals(postToBeUpdated.getAuthorId(), userAuthorIdFromDto)) {
            throw new DataValidationException(NON_MATCHING_AUTHORS_EXCEPTION.getMessage());
        }

        Long projectAuthorIdFromDto = postDto.getProjectId();
        if (projectAuthorIdFromDto != null && !Objects.equals(postToBeUpdated.getProjectId(), projectAuthorIdFromDto)) {
            throw new DataValidationException(NON_MATCHING_AUTHORS_EXCEPTION.getMessage());
        }

        Set<Long> likesOfPostToBeUpdated = postToBeUpdated.getLikes().stream()
                .map(Like::getId)
                .collect(Collectors.toSet());
        if (!likesOfPostToBeUpdated.containsAll(postDto.getLikesIds())) {
            throw new DataValidationException(LIKES_UPDATE_EXCEPTION.getMessage());
        }

        Set<Long> commentsOfPostToBeUpdated = postToBeUpdated.getComments().stream()
                .map(Comment::getId)
                .collect(Collectors.toSet());
        if (!commentsOfPostToBeUpdated.containsAll(postDto.getCommentsIds())) {
            throw new DataValidationException(LIKES_UPDATE_EXCEPTION.getMessage());
        }

        LocalDateTime publishedAtFromDto = postDto.getPublishedAt();
        if (publishedAtFromDto != null && postToBeUpdated.isPublished() && !postToBeUpdated.getPublishedAt().equals(publishedAtFromDto)) {
            throw new DataValidationException(PUBLISHED_DATE_UPDATE_EXCEPTION.getMessage());
        }

        if (!postDto.getDeleted().equals(postToBeUpdated.isDeleted())) {
            throw new DataValidationException(DELETED_STATUS_UPDATE_EXCEPTION.getMessage());
        }
    }

    private void checkAuthorExistence(Long authorId, Long projectId) {
        if (authorId != null) {
            checkUserExistence(authorId);
        }

        if (projectId != null) {
            checkProjectExistence(projectId);
        }
    }

    private Post getPost(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException(NON_EXISTING_POST_EXCEPTION.getMessage()));
    }

    private List<PostDto> getSortedDrafts(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    private List<PostDto> getSortedPosts(List<Post> posts) {
        return posts.stream()
                .filter(Post::isPublished)
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }
}
