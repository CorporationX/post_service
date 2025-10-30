package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;
    private final UserContext userContext;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostRepository postRepository;

    @Override
    @Transactional
    public PostDto createDraft(PostDto postDto) {
        validate(postDto);
        Post post = postMapper.toPost(postDto);
        post = postRepository.save(post);
        log.info("Draft #{} is created", post.getId());
        return postMapper.toPostDto(post);
    }

    @Override
    @Transactional
    public PostDto publishPost(long postId) {
        Post post = findPost(postId);
        validate(postMapper.toPostDto(post));
        if (post.isPublished()) {
            log.error("Post #{} has already been published", postId);
            throw new DataValidationException("This post has already been published");
        }
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        post = postRepository.save(post);
        log.info("Post #{} is published", postId);
        return postMapper.toPostDto(post);
    }

    @Override
    @Transactional
    public PostDto updatePost(long postId, PostDto postDto) {
        validate(postDto);
        Post currentPost = findPost(postId);
        if (!Objects.equals(currentPost.getAuthorId(), postDto.authorId())
                || !Objects.equals(currentPost.getProjectId(), postDto.projectId())) {
            log.error("Attempt to change author of the post");
            throw new DataValidationException("Unable to change author of the post");
        }
        postMapper.updatePost(currentPost, postDto);
        log.info("Post #{} is updated", postId);
        return postMapper.toPostDto(currentPost);
    }

    @Override
    @Transactional
    public PostDto deletePost(long postId) {
        Post post = findPost(postId);
        validate(postMapper.toPostDto(post));
        post.setDeleted(true);
        post = postRepository.save(post);
        log.info("Post #{} is deleted", postId);
        return postMapper.toPostDto(post);
    }

    @Override
    @Transactional
    public PostDto findPostById(long postId) {
        Post post = findPost(postId);
        if (!post.isPublished() || post.isDeleted()) {
            log.error("Post #{} is unpublished or deleted", postId);
            throw new DataValidationException("This post is either unpublished or deleted");
        }
        return postMapper.toPostDto(findPost(postId));
    }

    @Override
    @Transactional
    public List<PostDto> findDraftsByAuthorId(long authorId) {
        return postRepository.findByPublishedFalseAndAuthorIdAndDeletedFalseOrderByCreatedAtDesc(authorId).stream()
                .map(postMapper::toPostDto)
                .toList();
    }

    @Override
    @Transactional
    public List<PostDto> findDraftsByProjectId(long projectId) {
        return postRepository.findByPublishedFalseAndProjectIdAndDeletedFalseOrderByCreatedAtDesc(projectId).stream()
                .map(postMapper::toPostDto)
                .toList();
    }

    @Override
    @Transactional
    public List<PostDto> findPostsByAuthorId(long authorId) {
        return postRepository.findByPublishedTrueAndAuthorIdAndDeletedFalseOrderByPublishedAtDesc(authorId).stream()
                .map(postMapper::toPostDto)
                .toList();
    }

    @Override
    @Transactional
    public List<PostDto> findPostsByProjectId(long projectId) {
        return postRepository.findByPublishedTrueAndProjectIdAndDeletedFalseOrderByPublishedAtDesc(projectId).stream()
                .map(postMapper::toPostDto)
                .toList();
    }

    private void validate(PostDto postDto) {
        long currentUserId = userContext.getUserId();
        if (userServiceClient.getUserById(currentUserId) == null) {
            log.error("Action from unknown user detected");
            throw new EntityNotFoundException("User doesn't exist");
        }
        if (postDto.authorId() != null && postDto.authorId() != currentUserId) {
            log.error("Request includes two different User's IDs: #{} and #{}", currentUserId, postDto.authorId());
            throw new DataValidationException("User definition error is occur");
        }
        if (postDto.authorId() != null) {
            return;
        }
        ProjectDto currentProject = projectServiceClient.getProjectById(postDto.projectId());
        if (currentProject == null) {
            log.error("Project #{} doesn't exist", postDto.projectId());
            throw new EntityNotFoundException("Project doesn't exist");
        }
        if (currentProject.ownerId() != currentUserId) {
            log.error("User #{} is not Project #{} owner", currentUserId, currentProject.ownerId());
            throw new DataValidationException("User must be project owner");
        }
    }

    private Post findPost(long postId) {
        return postRepository.findById(postId).orElseThrow(() -> {
            log.error("Post #{} doesn't exist", postId);
            return new EntityNotFoundException("Post doesn't exist");
        });
    }
}
