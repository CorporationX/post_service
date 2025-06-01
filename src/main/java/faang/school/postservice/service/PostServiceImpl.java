package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.FileUploadException;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AuthorCacheRepository;
import faang.school.postservice.repository.PostRepository;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import faang.school.postservice.config.image.ImageDimensions;
import faang.school.postservice.config.image.ImageProcessingProperties;
import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.Optional;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Validated
@Transactional
@Slf4j
public class PostServiceImpl implements PostService {

    private static final String POST_NOT_EXIST = "Post doesn't exist";

    private final PostRepository postRepository;
    private final ResourceRepository resourceRepository;
    private final PostMapper postMapper;
    private final ResourceMapper resourceMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final ThreadPoolTaskScheduler taskScheduler;
    private final PostModerationDictionaryImpl moderationDictionary;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;
    private final AuthorCacheRepository authorCacheRepository;

    @Value("${app.scheduling.post.max-posts-per-time}")
    private int limitToModerate;

    @Value("${post-service.post.count-of-unverified-posts-to-ban}")
    private int countOfUnverifiedPostsToBan;
    private final ImageProcessingProperties properties;
    private final ImageResizer imageResizer;
    private final MinioClient minioClient;
    @Value("${s3.bucket-name}")
    private String bucketName;

    @PostConstruct
    public void init() {
        try {
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!isExist) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Bucket '{}' created successfully", bucketName);
            } else {
                log.info("Bucket '{}' already exists", bucketName);
            }
        } catch (Exception e) {
            log.error("Error while initializing MinIO bucket: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize MinIO bucket", e);
        }
    }

    @Override
    public PostDto createDraft(PostDto postDto) {
        validatePostDto(postDto);
        Post post = postMapper.toEntity(postDto);
        post.setPublished(false);
        post = postRepository.save(post);
        return postMapper.toDto(post);
    }

    @Override
    @Transactional
    public PostDto publishPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException(POST_NOT_EXIST));
        if (post.isPublished()) {
            throw new IllegalStateException("Post is already published");
        }
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        post = postRepository.save(post);
        authorCacheRepository.saveAuthor(postId, post.getAuthorId());
        return postMapper.toDto(post);
    }

    @Override
    public PostDto updatePost(Long postId, PostDto postDto) {
        validatePostDto(postDto);
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException(POST_NOT_EXIST));
        if (postDto.getAuthorId() != null && !postDto.getAuthorId().equals(post.getAuthorId())) {
            throw new IllegalArgumentException("Cannot change the author of the post");
        }
        if (postDto.getProjectId() != null && !postDto.getProjectId().equals(post.getProjectId())) {
            throw new IllegalArgumentException("Cannot change the project of the post");
        }
        postMapper.update(postDto, post);
        post = postRepository.save(post);
        return postMapper.toDto(post);
    }

    @Override
    public PostDto softDelete(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException(POST_NOT_EXIST));
        post.setDeleted(true);
        post = postRepository.save(post);
        return postMapper.toDto(post);
    }

    @Override
    public PostDto getPostById(Long postId) {
        return postMapper.toDto(postRepository
                .findByIdWithLikes(postId).orElseThrow(() -> new NotFoundException("The post hasn't been found")));
    }

    @Override
    public List<PostDto> getAllDraftsByAuthorId(Long authorId) {
        return postRepository.findByAuthorId(authorId).stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    public List<PostDto> getAllDraftsByProjectId(Long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        return posts.stream()
                .filter(post -> !post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    public List<PostDto> getAllPublishedPostsByAuthorId(Long authorId) {
        List<Post> posts = postRepository.findByAuthorIdWithLikes(authorId);
        return posts.stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    public List<ResourceDto> uploadImageToPost(Long postId, List<MultipartFile> files) {
        log.info("Starting image upload for postId: {}, files count: {}", postId, files.size());
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("Post doesn't exist"));
        List<Resource> savedResources = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                log.debug("Processing file: {} (size: {} bytes)", file.getOriginalFilename(), file.getSize());
                BufferedImage bufferedImage = resizeImage(file);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();

                Resource processedResource = createResource(file);
                processedResource.setSize(imageBytes.length);
                processedResource.setPost(post);

                uploadToMinio(processedResource, new ByteArrayInputStream(imageBytes));
                log.info("Successfully uploaded to MinIO: {}", processedResource.getKey());

                Resource savedResource = resourceRepository.save(processedResource);
                log.debug("Saved resource to DB with id: {}, key: {}",
                        savedResource.getId(), savedResource.getKey());
                savedResources.add(savedResource);
            }
        } catch (Exception e) {
            rollbackUpload(e, savedResources);
            throw new FileUploadException("File couldn't be uploaded" + e);
        }
        log.info("Successfully uploaded {} images for postId: {}", savedResources.size(), postId);
        return savedResources.stream().map(resourceMapper::toDto).toList();
    }

    private void rollbackUpload(Exception e, List<Resource> savedResources) {
        log.error("Error during image upload. Rolling back changes", e);
        savedResources.forEach(resource -> {
            try {
                if (resource.getKey() != null) {
                    minioClient.removeObject(
                            RemoveObjectArgs.builder()
                                    .bucket(bucketName)
                                    .object(resource.getKey())
                                    .build());
                }
            } catch (Exception ex) {
                log.error("Failed to cleanup MinIO object: {}", resource.getKey(), ex);
            }
            resourceRepository.delete(resource);
        });
    }

    @Override
    public List<PostDto> getAllPublishedPostsByProjectId(Long projectId) {
        List<Post> posts = postRepository.findByProjectIdWithLikes(projectId);
        return posts.stream()
                .filter(post -> post.isPublished() && !post.isDeleted())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    @Override
    public void moderatePosts() {
        List<Post> batch;
        int processedTotal = 0;
        while (!(batch = postRepository.findUnverifiedPosts(limitToModerate)).isEmpty()){
            List<CompletableFuture<Void>> futures = batch.stream()
                    .map(post -> CompletableFuture.runAsync(
                            () -> moderatePost(post),
                            taskScheduler.getScheduledExecutor()
                    ))
                    .toList();
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            processedTotal += batch.size();
            log.info("Processed batch of {} posts. Total processed: {}", batch.size(), processedTotal);
        }
    }

    public Post getPostEntryById(@Min(1) long id) {
        log.debug("Fetching post with ID: {}", id);

        log.debug("Search for fasting in the database");
        Optional<Post> postOptional = postRepository.findById(id);
        if (postOptional.isEmpty()) {
            log.error("Post with ID {} not found", id);
            throw new EntityNotFoundException("Post not found");
        }
        log.debug("Post with ID {} fetched successfully", id);
        return postOptional.get();
    }

    private void moderatePost(Post post) {
        boolean isClean = moderationDictionary.isTextWithoutForbiddenWords(post.getContent());
        post.setVerified(isClean);
        post.setVerifiedDate(LocalDateTime.now());
        postRepository.save(post);
        log.debug("Post {} moderated. Status: {}", post.getId(), isClean);
    }

    @Override
    public Post findPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post with id: " + id + " not found"));
    }

    @Override
    public void removeTagsFromPost(Long postId, List<Long> tagsId) {
        postRepository.deleteTagsFromPost(postId, tagsId);
    }

    @Override
    public void banUsersWithTooManyOffendedPosts() {
        List<Post> rejectedPosts = postRepository.findByVerifiedFalse();

        Map<Long, Long> authorRejectedCount = rejectedPosts.stream()
                .collect(Collectors.groupingBy(Post::getAuthorId, Collectors.counting()));

        List<Long> userIdsToBan = authorRejectedCount.entrySet().stream()
                .filter(user -> user.getValue() > countOfUnverifiedPostsToBan)
                .map(Map.Entry::getKey)
                .toList();
        log.info("Users ids {} sent to be baned", userIdsToBan);

        userIdsToBan.forEach(userId -> redisTemplate.convertAndSend(channelTopic.getTopic(), userId.toString()));
        log.info("Information to ban intruders has been sent via redis");
    }

    private void validatePostDto(PostDto postDto) {
        if (postDto.getAuthorId() != null && postDto.getProjectId() != null) {
            throw new IllegalArgumentException("Post can have only one author: either user or project");
        }
        if (postDto.getAuthorId() != null) {
            UserDto userDto = userServiceClient.getUser(postDto.getAuthorId());
            if (userDto == null) {
                throw new NotFoundException("Author doesn't exist");
            }
        }
//        TODO: remove // when ProjectService has necessary Controller
//        if (postDto.getProjectId() != null) {
//            ProjectDto projectDto = projectServiceClient.getProject(postDto.getProjectId());
//            if (projectDto == null) {
//                throw new NotFoundException("Project doesn't exist");
//            }
//        }
    }

    private BufferedImage resizeImage(MultipartFile file) {
        if (!properties.getAllowedContentTypes().contains(file.getContentType())) {
            log.error("Sent photo with ContentType: {}", file.getContentType());
            throw new DataValidationException("Illegal type of the image");
        }

        BufferedImage image;
        try {
            image = ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            log.error("Image can't be read by the application when uploading");
            throw new DataValidationException("Can't read the image");
        }
        if (image == null) {
            log.error("File can't be read because it's not an image");
            throw new DataValidationException("The file is not an image");
        }

        int width = image.getWidth();
        int height = image.getHeight();
        ImageDimensions targetDimensions = (width > height) ?
                properties.getResize().getHorizontal() : properties.getResize().getSquare();

        if (width > targetDimensions.getWidth() || height > targetDimensions.getHeight()) {
            try {
                image = imageResizer.resize(image, targetDimensions.getWidth(), targetDimensions.getHeight());
            } catch (IOException e) {
                log.error("Image can't be resized: {}", file.getName());
                throw new DataValidationException("Wrong image size. Can't be resized");
            }
        }
        return image;
    }

    private Resource createResource(MultipartFile originalFile) {
        return Resource.builder()
                .name(generateFileName(originalFile))
                .type("image/jpeg")
                .size(originalFile.getSize())
                .key("posts/" + UUID.randomUUID() + getFileExtension(originalFile))
                .build();
    }

    private String generateFileName(MultipartFile file) {
        return StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
    }

    private String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            return ".jpg";
        }
        int dotIndex = originalFilename.lastIndexOf(".");
        return dotIndex > 0 ? originalFilename.substring(dotIndex) : ".jpg";
    }

    private void uploadToMinio(Resource resource, InputStream inputStream) {
        try {
            byte[] imageBytes = inputStream.readAllBytes();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(resource.getKey())
                            .stream(new ByteArrayInputStream(imageBytes), imageBytes.length, -1)
                            .contentType(resource.getType() != null ?
                                    resource.getType() : "image/jpeg")
                            .build());
        } catch (Exception e) {
            throw new FileUploadException("File couldn't be uploaded to MinIO: " + e);
        }
    }
}
