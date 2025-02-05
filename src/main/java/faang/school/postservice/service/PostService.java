package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.PostConfig;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostReadDto;
import faang.school.postservice.dto.post.PostOwnerType;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.exception.BusinessException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserContext userContext;
    private final PostConfig postConfig;
    private final ExecutorService executorService;


    public PostReadDto createPostDraft(PostCreateDto dto) {
        validateCreateDraftDto(dto);

        Post post = postMapper.toEntity(dto);
        post = postRepository.save(post);
        return postMapper.toDto(post);
    }

    public PostReadDto publishPost(long id) {
        Post post = getPostById(id);
        if (post.isPublished()) {
            throw new BusinessException("Пост уже опубликован");
        }
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        return postMapper.toDto(postRepository.save(post));
    }

    public PostReadDto updatePost(long id, PostUpdateDto dto) {
        Post post = getPostById(id);
        if (post.isDeleted()) {
            throw new BusinessException("Пост удалён");
        }
        postMapper.updateEntityFromDto(dto, post);
        return postMapper.toDto(postRepository.save(post));
    }

    public PostReadDto softDeletePost(long id) {
        Post post = getPostById(id);
        if (post.isDeleted()) {
            throw new BusinessException("Пост уже удален");
        }
        post.setDeleted(true);
        return postMapper.toDto(postRepository.save(post));
    }

    public List<PostReadDto> getAllDrafts(long id, PostOwnerType ownerType) {
        return getAllPostByCondition(
                ownerType,
                () -> postRepository.findAllDraftsByAuthorId(id),
                () -> postRepository.findAllDraftsByProjectId(id)
        );
    }

    public List<PostReadDto> getAllPublished(long id, PostOwnerType ownerType) {
        return getAllPostByCondition(
                ownerType,
                () -> postRepository.findAllPublishedByAuthorId(id),
                () -> postRepository.findAllPublishedByProjectId(id)
        );
    }

    public Post getPostById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пост с ID " + id + " не найден"));
    }

    private List<PostReadDto> getAllPostByCondition(
            PostOwnerType ownerType,
            Supplier<List<Post>> authorSupplier,
            Supplier<List<Post>> projetcSupplier
    ) {
        List<Post> postStream = switch (ownerType) {
            case AUTHOR:
                yield authorSupplier.get();
            case PROJECT:
                yield projetcSupplier.get();
        };
        return postStream.stream()
                .map(postMapper::toDto)
                .toList();
    }

    private void validateCreateDraftDto(PostCreateDto dto) {
        var authorId = dto.getAuthorId();
        var projectId = dto.getProjectId();

        if (authorId != null && projectId != null) {
            throw new BusinessException("Пост может создать либо автор, либо проект.");
        }

        if (authorId != null) {
            userContext.setUserId(authorId);
            if (userServiceClient.getUser(authorId) == null) {
                throw new EntityNotFoundException("Пользователь не найден");
            }
        } else if (projectId != null) {
            userContext.setUserId(projectId);
            if (projectServiceClient.getProject(projectId) == null) {
                throw new EntityNotFoundException("Проект не найден");
            }
        }
    }

    public void publishScheduledPosts() {
        int batchSize = postConfig.getBatchSize();
        int page = 0;

        while (true) {
            Page<Post> postPage = postRepository.findReadyToPublish(PageRequest.of(page, batchSize));

            if (postPage.isEmpty()) break;

            List<Post> posts = postPage.getContent();
            executorService.submit(() -> publishBatch(posts));
            page++;
        }
    }

    private void publishBatch(List<Post> posts) {
        try {
            posts.forEach(post -> {
                post.setPublished(true);
                post.setPublishedAt(LocalDateTime.now());
            });

            postRepository.saveAll(posts);
        } catch (Exception e) {
            log.error("Ошибка при публикации батча из {} постов", posts.size(), e);
        }
    }
}
