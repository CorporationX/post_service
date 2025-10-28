package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.common.PageResponse;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exeption.DataValidationException;
import faang.school.postservice.exeption.ForbiddenException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.spec.PostSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final UserContext userContext;

    public PostDto createPostAsDraft(PostCreateDto postCreateDto) {
        long userId = userContext.getUserId();
        UserDto user = userServiceClient.getUser(userId);

        Post post = PostMapper.toEntity(postCreateDto);

        if (postCreateDto.projectId() != null) {
            ProjectDto project = projectServiceClient.getProject(postCreateDto.projectId());
            post.setProjectId(project.id());
        } else {
            post.setAuthorId(userId);
        }

        Post savedPost = postRepository.save(post);
        return PostMapper.toDto(savedPost);
    }

    public PostDto publishPost(Long postId) {
        long userId = userContext.getUserId();
        UserDto user = userServiceClient.getUser(userId);
        Post post = postRepository.getByIdOrThrow(postId);

        if (post.isPublished()) {
            throw new ForbiddenException("Post id=%s is already published".formatted(post.getId()));
        }

        validateUpdatingPost(post, userId);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        Post saved = postRepository.save(post);
        return PostMapper.toDto(saved);
    }

    public PostDto updatePost(Long postId, PostUpdateDto postUpdateDto) {
        long userId = userContext.getUserId();
        UserDto user = userServiceClient.getUser(userId);
        Post post = postRepository.getByIdOrThrow(postId);

        validateUpdatingPost(post, userId);
        PostMapper.update(post, postUpdateDto);

        Post saved = postRepository.save(post);
        return PostMapper.toDto(saved);
    }

    public void deletePostSoftly(Long postId) {
        long userId = userContext.getUserId();
        UserDto user = userServiceClient.getUser(userId);
        Post post = postRepository.getByIdOrThrow(postId);

        if (post.isDeleted()) {
            throw new ForbiddenException("Post id=%s is already deleted".formatted(post.getId()));
        }

        validateUpdatingPost(post, userId);

        post.setDeleted(true);
        postRepository.save(post);
    }

    public PageResponse<PostDto> findAllPublishedByFilter(
            Long authorId,
            Long projectId,
            Pageable pageable
    ) {
        if ((authorId == null && projectId == null) || (authorId != null && projectId != null)) {
            throw new DataValidationException("Use authorId or projectId, not both");
        }

        Specification<Post> spec = PostSpecification.filter(authorId, projectId, true);
        Page<Post> page = postRepository.findAll(spec, pageable);

        return new PageResponse<>(
                page.map(PostMapper::toDto).getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    public PageResponse<PostDto> findAllDraftsByAuthor(Pageable pageable) {
        long userId = userContext.getUserId();
        UserDto user = userServiceClient.getUser(userId);

        Specification<Post> spec = PostSpecification.filter(userId, null, false);
        Page<Post> page = postRepository.findAll(spec, pageable);

        return new PageResponse<>(
                page.map(PostMapper::toDto).getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    public PageResponse<PostDto> findAllDraftsByProject(Long projectId, Pageable pageable) {
        long userId = userContext.getUserId();
        UserDto user = userServiceClient.getUser(userId);

        validateProjectOwner(projectId, userId);

        Specification<Post> spec = PostSpecification.filter(null, projectId, false);
        Page<Post> page = postRepository.findAll(spec, pageable);

        return new PageResponse<>(
                page.map(PostMapper::toDto).getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    public PostDto findById(Long postId) {
        long userId = userContext.getUserId();
        UserDto user = userServiceClient.getUser(userId);
        Post post = postRepository.getByIdOrThrow(postId);
        return PostMapper.toDto(post);
    }

    private void validateUpdatingPost(Post post, long userId) {
        if (post.getAuthorId() != null) {
            if (!post.getAuthorId().equals(userId)) {
                throw new ForbiddenException("User id=%s is not author of post".formatted(userId));
            }
        } else if (post.getProjectId() != null) {
            validateProjectOwner(post.getProjectId(), userId);
        }
    }

    private void validateProjectOwner(Long projectId, Long userId) {
        ProjectDto project = projectServiceClient.getProject(projectId);
        if (project.ownerId() != userId) {
            throw new ForbiddenException(
                    "User id=%s is not owner of project id=%s".formatted(userId, project.id())
            );
        }
    }


}
