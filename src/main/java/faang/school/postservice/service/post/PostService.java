package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.config.props.PostProperties;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostOwnerType;
import faang.school.postservice.dto.post.PostReadDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.exception.BusinessException;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.HashtagService;
import faang.school.postservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final HashtagService hashtagService;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserContext userContext;
    private final PostSchedulerService postSchedulerService;
    private final PostProperties postProperties;
    private final S3Service s3Service;
    private final ResourceRepository resourceRepository;
    private final PostImageService postImageService;

    @Value("${post.schedule.batch-size}")
    private int batchSize;

    @Value("${post.upload.max-files}")
    private int maxFiles;

    @Value("${post.upload.max-file-size-mb}")
    private int maxFileSizeMb;

    public PostReadDto createPostDraft(PostCreateDto dto) {
        validateCreateDraftDto(dto);
        verifyHashtagsExists(dto.getHashtagIds());

        Post post = postMapper.toEntity(dto);
        if (dto.getHashtagIds() != null) {
            List<Hashtag> hashtags = dto.getHashtagIds().stream()
                    .map(hashtagService::getHashtagById)
                    .toList();
            post.setHashtags(hashtags);
        }

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
        verifyHashtagsExists(dto.getHashtagIds());

        postMapper.updateEntityFromDto(dto, post);

        if (dto.getHashtagIds() != null) {
            List<Hashtag> hashtags = dto.getHashtagIds().stream()
                    .map(hashtagService::getHashtagById)
                    .toList();
            post.setHashtags(hashtags);
        }

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

    public List<PostReadDto> getPostsByHashtagId(long hashtagId) {
        List<Post> posts = postRepository.findAllByHashtagId(hashtagId);
        return posts.stream()
                .filter(post -> !post.isDeleted())
                .filter(Post::isPublished)
                .map(postMapper::toDto)
                .toList();
    }

    public PostReadDto uploadImages(long postId, List<MultipartFile> images) {
        Post post = getPostById(postId);
        validateImageUpload(images, post.getResources().size());

        String folder = postId + "_post_attachments";
        List<Resource> newResources = images.stream()
                .map(file -> s3Service.uploadResource(file, folder)).toList();

        resourceRepository.saveAll(newResources);
        post.getResources().addAll(newResources);

        post = postRepository.save(post);
        return postMapper.toDto(post);
    }

    public PostReadDto deleteImages(Long postId, List<String> fileKeys) {
        Post post = getPostById(postId);

        for (String fileKey : fileKeys) {
            Resource resource = resourceRepository.findByKey(fileKey);
            if (resource != null) {
                post.getResources().remove(resource);
                resourceRepository.delete(resource);
                s3Service.deleteFile(fileKey);
            }
        }

        post = postRepository.save(post);
        return postMapper.toDto(post);
    }

    public byte[] downloadImage(String fileKey) {
        Resource resource = resourceRepository.findByKey(fileKey);
        try (InputStream inputStream = s3Service.downloadFile(resource.getKey())) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки файла: " + e.getMessage());
        }
    }

    private long getMaxFileSizeBytes() {
        return maxFileSizeMb * 1024L * 1024L;
    }

    private void validateImageUpload(List<MultipartFile> files, int currentSize) {
        int totalSize = currentSize + files.size();

        if (totalSize > maxFiles) {
            throw new BusinessException("Максимум можно загрузить " + maxFiles + " файлов");
        }
        for (MultipartFile file : files) {
            if (file.getSize() > getMaxFileSizeBytes()) {
                file = postImageService.getResizedCover(file);
            }
            String fileType = file.getContentType();
            if (fileType == null || !fileType.startsWith("image/")) {
                throw new DataValidationException("Неверный тип файла. Разрешено загружать только изображения");
            }
        }
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

    private void verifyHashtagsExists(List<Long> hashtagIds) {
        if (hashtagIds == null) {
            return;
        }
        List<Long> missingHashtagIds = hashtagIds.stream()
                .filter(hashtagId -> !hashtagService.isHashtagExist(hashtagId))
                .toList();

        if (!missingHashtagIds.isEmpty()) {
            throw new EntityNotFoundException("Хэштеги со ID: " + missingHashtagIds + " не найдены");
        }
    }

    public void publishScheduledPosts() {
        postSchedulerService.publishScheduledPosts(
                postProperties.getSchedule().getBatchSize()
        );
    }
}