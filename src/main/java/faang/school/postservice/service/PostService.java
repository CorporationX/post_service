package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.AlreadyDeletedException;
import faang.school.postservice.exception.AlreadyPostedException;
import faang.school.postservice.exception.NoPublishedPostException;
import faang.school.postservice.exception.SamePostAuthorException;
import faang.school.postservice.exception.UpdatePostException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserServiceClient userService;
    private final ProjectServiceClient projectService;
    private final PostMapper postMapper;

    public PostDto crateDraftPost(PostDto postDto) {
        validateData(postDto);

        Post savedPost = postRepository.save(postMapper.toPost(postDto));
        log.info("Draft post was created successfully, draftId={}", savedPost.getId());
        return postMapper.toDto(savedPost);
    }

    public PostDto publishPost(long postId) {
        validatePostId(postId);

        List<Post> readyToPublishPost = postRepository.findReadyToPublish().stream()
                .filter(post -> post.getId() == postId)
                .toList();
        if (readyToPublishPost.isEmpty()) {
            throw new AlreadyPostedException("You cannot publish a post that has already been published or deleted");
        }

        Post post = readyToPublishPost.get(0);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        log.info("Post was published successfully, postId={}", post.getId());
        return postMapper.toDto(post);
    }

    public PostDto updatePost(PostDto updatePost) {
        long postId = updatePost.getId();
        validatePostId(postId);

        Post post = postRepository.findById(postId).get();
        validateAuthorUpdate(post, updatePost);

        post.setContent(updatePost.getContent());
        post.setUpdatedAt(LocalDateTime.now());
        log.info("Post was updated successfully, postId={}", post.getId());
        return postMapper.toDto(post);
    }

    public PostDto softDelete(long postId) {
        validatePostId(postId);

        Post post = postRepository.findById(postId).get();

        if (post.isDeleted()) {
            throw new AlreadyDeletedException("Post has been already deleted");
        }
        post.setDeleted(true);
        log.info("Post was soft-deleted successfully, postId={}", postId);
        return postMapper.toDto(post);
    }

    public PostDto getPost(long postId) {
        validatePostId(postId);

        Post post = postRepository.findById(postId).get();
        if (post.isDeleted()) {
            throw new AlreadyDeletedException("This post has been already deleted");
        }
        if (!post.isPublished()) {
            throw new NoPublishedPostException("This post hasn't been published yet");
        }

        log.info("Post has taken from DB successfully, postId={}", postId);
        return postMapper.toDto(post);
    }

    public List<PostDto> getUserDrafts(long userId) {
        validateUserId(userId);

        List<PostDto> userDrafts = postRepository.findByAuthorId(userId).stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .map(postMapper::toDto)
                .toList();

        log.info("User's drafts have taken from DB successfully, userId={}", userId);
        return userDrafts;
    }

    public List<PostDto> getProjectDrafts(long projectId) {
        validateProjectId(projectId);

        List<PostDto> projectDrafts = postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .map(postMapper::toDto)
                .toList();

        log.info("Drafts of project have taken from DB successfully, projectId={}", projectId);
        return projectDrafts;
    }

    public List<PostDto> getUserPosts(long userId) {
        validateUserId(userId);

        List<PostDto> userPosts = postRepository.findByAuthorId(userId).stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .map(postMapper::toDto)
                .toList();

        log.info("User's posts have taken from DB successfully, userId={}", userId);
        return userPosts;
    }

    public List<PostDto> getProjectPosts(long projectId) {
        validateProjectId(projectId);

        List<PostDto> projectPosts = postRepository.findByProjectId(projectId).stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .map(postMapper::toDto)
                .toList();

        log.info("Posts of project have taken from DB successfully, projectId={}", projectId);
        return projectPosts;
    }

    private void validatePostId(long postId) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("This post does not exist");
        }
    }

    private void validateData(PostDto postDto) {
        Long userId = postDto.getAuthorId();
        Long projectId = postDto.getProjectId();

        if (userId != null && projectId != null) {
            throw new SamePostAuthorException("The author of the post cannot be both a user and a project");
        }
        if (userId != null) {
           validateUserId(userId);
        } else {
           validateProjectId(projectId);
        }
    }

    private void validateAuthorUpdate(Post post, PostDto updatePost) {
        Long authorId = post.getAuthorId();
        Long projectId = post.getProjectId();
        Long updateAuthorId = updatePost.getAuthorId();
        Long updateProjectId = updatePost.getProjectId();

        if (authorId != null) {
            if (updateAuthorId == null || updateAuthorId != authorId) {
                throw new UpdatePostException("Author of the post cannot be deleted or changed");
            }
        } else {
            if (updateProjectId == null || updateProjectId != projectId) {
                throw new UpdatePostException("Author of the post cannot be deleted or changed");
            }
        }
    }

    private void validateUserId(long id) {
        try {
            userService.getUser(id);
        } catch (FeignException e) {
            throw new EntityNotFoundException("This user is not found");
        }
    }

    private void validateProjectId(long id) {
        try {
            projectService.getProject(id);
        } catch (FeignException e) {
            throw new EntityNotFoundException("This project is not found");
        }
    }
}