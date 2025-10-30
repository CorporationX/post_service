package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.CreatePostRequestDto;
import faang.school.postservice.dto.post.UpdatePostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ProjectNotFoundException;
import faang.school.postservice.exception.UserNotFoundException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    @Override
    public PostResponseDto createDraft(CreatePostRequestDto dto) {
        log.info("Creating draft: authorId={}, projectId={}", dto.authorId(), dto.projectId());

        if (!dto.isExactlyOneAuthor()) {
            log.warn("Draft creation error: exactly one author must be specified (authorId XOR projectId)");
            throw new IllegalArgumentException("Exactly one author must be specified: authorId or projectId");
        }
        if (dto.content() == null || dto.content().isBlank()) {
            log.warn("Draft creation error: content is empty");
            throw new IllegalArgumentException("content must not be empty");
        }

        try {
            if (dto.authorId() != null) {
                ResponseEntity<UserDto> resp = userServiceClient.getUser(dto.authorId());
                if (resp.getStatusCode() == HttpStatus.NOT_FOUND) {
                    throw new UserNotFoundException(dto.authorId());
                }
            } else {
                ResponseEntity<ProjectDto> resp = projectServiceClient.getProject(dto.projectId());
                if (resp.getStatusCode() == HttpStatus.NOT_FOUND) {
                    throw new ProjectNotFoundException(dto.projectId());
                }
            }
        } catch (FeignException.NotFound ex) {
            log.warn("Author not found in external service: {}", ex.getMessage());
            throw new EntityNotFoundException("Author not found in external service");
        }

        Post draft = postMapper.toEntity(dto);
        Post saved = postRepository.save(draft);
        log.info("Draft created id={}", saved.getId());
        return postMapper.toDto(saved);
    }

    @Override
    public PostResponseDto publish(long id) {
        log.info("Publishing post id={}", id);
        Post post = getPostEntityById(id);

        if (post.isDeleted()) {
            log.warn("Attempt to publish a deleted post id={}", id);
            throw new IllegalStateException("Cannot publish a deleted post");
        }
        if (post.isPublished()) {
            log.warn("Attempt to republish an already published post id={}", id);
            throw new IllegalStateException("Post is already published");
        }

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        Post saved = postRepository.save(post);

        log.info("Post published id={} at {}", id, saved.getPublishedAt());
        return postMapper.toDto(saved);
    }

    @Override
    public PostResponseDto update(long id, UpdatePostRequestDto dto) {
        log.info("Updating post id={}", id);
        Post post = getPostEntityById(id);

        if (post.isDeleted()) {
            log.warn("Attempt to update a deleted post id={}", id);
            throw new IllegalStateException("Cannot update a deleted post");
        }

        if (dto.content() != null) {
            if (dto.content().isBlank()) {
                log.warn("Post update error id={}: content is empty", id);
                throw new IllegalArgumentException("content must not be empty");
            }
            postMapper.updateEntityFromDto(dto, post);
            post.setUpdatedAt(LocalDateTime.now());
        }

        Post saved = postRepository.save(post);
        log.info("Post id={} updated", id);
        return postMapper.toDto(saved);
    }

    @Override
    public void softDelete(long id) {
        log.info("Soft deleting post id={}", id);
        Post post = getPostEntityById(id);

        if (!post.isDeleted()) {
            post.setDeleted(true);
            post.setPublished(false);
            post.setUpdatedAt(LocalDateTime.now());
            postRepository.save(post);
            log.info("Post id={} marked as deleted (published=false)", id);
        } else {
            log.debug("Post id={} was already deleted earlier", id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponseDto getById(long id) {
        log.debug("Fetching post by id={}", id);
        return postMapper.toDto(getPostEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponseDto> getDraftsByUser(long userId) {
        log.debug("Fetching drafts for userId={}", userId);
        return postRepository.findByAuthorId(userId).stream()
                .filter(p -> !p.isDeleted() && !p.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponseDto> getDraftsByProject(long projectId) {
        log.debug("Fetching drafts for projectId={}", projectId);
        return postRepository.findByProjectId(projectId).stream()
                .filter(p -> !p.isDeleted() && !p.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponseDto> getPublishedByUser(long userId) {
        log.debug("Fetching published posts for userId={}", userId);
        return postRepository.findByAuthorId(userId).stream()
                .filter(p -> !p.isDeleted() && p.isPublished())
                .sorted(Comparator.comparing(Post::getPublishedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponseDto> getPublishedByProject(long projectId) {
        log.debug("Fetching published posts for projectId={}", projectId);
        return postRepository.findByProjectId(projectId).stream()
                .filter(p -> !p.isDeleted() && p.isPublished())
                .sorted(Comparator.comparing(Post::getPublishedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(postMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Post getPostEntityById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Post not found with id={}", id);
                    return new EntityNotFoundException("Post not found with id: " + id);
                });
    }
}