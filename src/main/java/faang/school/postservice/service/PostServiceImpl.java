package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.AuthorNotFoundException;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.ExternalServiceException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    @Override
    @Transactional
    public PostDto createDraft(PostDto dto) {
        validateAuthor(dto.authorId(), dto.projectId());
        Post post = postMapper.toEntity(dto);
        return postMapper.toDto(postRepository.save(post));
    }

    @Override
    @Transactional
    public PostDto publishPost(Long postId) {
        Post post = getExistingPost(postId);
        if (post.isPublished()) {
            throw new DataValidationException("Post with id=" + postId + " is already published");
        }
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        return postMapper.toDto(postRepository.save(post));
    }

    @Override
    @Transactional
    public PostDto updatePost(Long postId, PostDto dto) {
        Post post = getExistingPost(postId);
        validateAuthorUnchanged(post, dto);
        post.setContent(dto.content());
        return postMapper.toDto(postRepository.save(post));
    }

    @Override
    @Transactional
    public void deletePost(Long postId) {
        Post post = getExistingPost(postId);
        post.setDeleted(true);
        postRepository.save(post);
    }

    @Override
    @Transactional(readOnly = true)
    public PostDto getPost(Long postId) {
        Post post = getExistingPost(postId);
        return postMapper.toDto(post);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> getAllDraftsByAuthorId(Long userId) {
        return postRepository.findDraftsByAuthor(userId).stream()
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> getAllDraftsByProjectId(Long projectId) {
        return postRepository.findDraftsByProject(projectId).stream()
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> getAllPostsByAuthorId(Long userId) {
        return postRepository.findPublishedByAuthor(userId).stream()
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> getAllPostsByProjectId(Long projectId) {
        return postRepository.findPublishedByProject(projectId).stream()
                .map(postMapper::toDto)
                .toList();
    }

    private Post getExistingPost(Long id) {
        return postRepository.findById(id)
                .filter(post -> !post.isDeleted())
                .orElseThrow(() ->
                        new PostNotFoundException("Post with id=" + id + " not found or has been deleted"));
    }

    private void validateAuthorUnchanged(Post post, PostDto dto) {
        if (!post.getAuthorId().equals(dto.authorId()) ||
                (post.getProjectId() != null && !post.getProjectId().equals(dto.projectId()))) {
            throw new DataValidationException("Post author cannot be changed (postId=" + post.getId() + ")");
        }
    }

    private void validateAuthor(Long authorId, Long projectId) {
        if ((authorId == null && projectId == null) || (authorId != null && projectId != null)) {
            throw new DataValidationException("Author must be either a user or a project, but not both or neither");
        }

        try {
            if (authorId != null) {
                userServiceClient.getUser(authorId);
            } else {
                projectServiceClient.getProject(projectId);
            }
        } catch (FeignException e) {
            String type = authorId != null ? "User" : "Project";
            Long id = authorId != null ? authorId : projectId;

            if (e.status() == 404) {
                throw new AuthorNotFoundException(type + " with id=" + id + " not found");
            } else if (e.status() >= 400 && e.status() < 500) {
                throw new DataValidationException(
                        type + " service returned client error (" + e.status() + ") for id=" + id);
            } else if (e.status() >= 500) {
                throw new ExternalServiceException(
                        type + " service unavailable or failed (" + e.status() + ") for id=" + id);
            } else {
                throw new RuntimeException("Unexpected error while validating author: " + e.getMessage());
            }
        }
    }
}
