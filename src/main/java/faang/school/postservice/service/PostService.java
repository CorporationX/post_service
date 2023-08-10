package faang.school.postservice.service;

import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.post.ResponsePostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final ResponsePostMapper responsePostMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    @Transactional(readOnly = true)
    public List<ResponsePostDto> getAllDraftByAuthor(Long authorId) {
        return postRepository.findByAuthorId(authorId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .map(responsePostMapper::toDto)
                .sorted(Comparator.comparing(ResponsePostDto::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResponsePostDto> getAllPublishedByAuthor(Long authorId) {
        return postRepository.findByAuthorId(authorId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .map(responsePostMapper::toDto)
                .sorted(Comparator.comparing(ResponsePostDto::getPublishedAt).reversed())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResponsePostDto> getAllDraftByProject(Long projectId) {
        return postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .map(responsePostMapper::toDto)
                .sorted(Comparator.comparing(ResponsePostDto::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResponsePostDto> getAllPublishedByProject(Long projectId) {
        return postRepository.findByProjectId(projectId).stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .map(responsePostMapper::toDto)
                .sorted(Comparator.comparing(ResponsePostDto::getPublishedAt).reversed())
                .collect(Collectors.toList());


    @Transactional
    public ResponsePostDto publish(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post is not found"));

        if (post.isPublished()) {
            throw new IllegalArgumentException("Can't publish already published post");
        }
        if (post.isDeleted()) {
            throw new IllegalArgumentException("Post has been deleted");
        }

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        return responsePostMapper.toDto(post);
    }

    @Transactional
    public ResponsePostDto update(UpdatePostDto dto) {
        Post post = postRepository.findById(dto.getId()).orElseThrow(() -> new IllegalArgumentException("Post is not found"));

        post.setContent(dto.getContent());
        post.setUpdatedAt(LocalDateTime.now());

        return responsePostMapper.toDto(post);
    }


    @Transactional(readOnly = true)
    public ResponsePostDto getById(Long id) {
        return responsePostMapper.toDto(
                postRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Post is not found"))
        );
    }

    @Transactional
    public ResponsePostDto softDelete(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post is not found"));

        post.setDeleted(true);

        return responsePostMapper.toDto(post);
    }

    @Transactional
    public ResponsePostDto createDraft(CreatePostDto dto) {
        Post post = new Post();

        processOwner(dto, post);

        post.setContent(dto.getContent());
        post.setCreatedAt(LocalDateTime.now());
        post.setPublished(false);
        post.setDeleted(false);

        return responsePostMapper.toDto(postRepository.save(post));
    }

    private void processOwner(CreatePostDto dto, Post post) {
        if (dto.getAuthorId() != null && dto.getProjectId() != null) {
            throw new IllegalArgumentException("Both AuthorId and ProjectId can't be not null");
        }
        if (dto.getAuthorId() != null) {
            UserDto userDto = Objects.requireNonNull(userServiceClient.getUser(dto.getAuthorId()));
            post.setAuthorId(userDto.getId());
        }
        if (dto.getProjectId() != null) {
            ProjectDto projectDto = Objects.requireNonNull(projectServiceClient.getProject(dto.getProjectId()));
            post.setProjectId(projectDto.getId());
        }
    }
}
