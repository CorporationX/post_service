package faang.school.postservice.service.post;

import faang.school.postservice.annotations.SendPostCreatedEvent;
import faang.school.postservice.annotations.SendPostViewEventToAnalytics;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.serializable.PostCacheDto;
import faang.school.postservice.dto.user.UserNewsFeedDto;
import faang.school.postservice.exception.ResourceNotFoundException;
import faang.school.postservice.exception.post.PostNotFoundException;
import faang.school.postservice.exception.post.PostPublishedException;
import faang.school.postservice.exception.post.image.DownloadImageFromPostException;
import faang.school.postservice.exception.post.image.UploadImageToPostException;
import faang.school.postservice.exception.spelling_corrector.DontRepeatableServiceException;
import faang.school.postservice.exception.spelling_corrector.RepeatableServiceException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.publisher.kafka.KafkaEventProducer;
import faang.school.postservice.publisher.kafka.events.PostEvent;
import faang.school.postservice.publisher.kafka.events.PostViewEvent;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.aws.s3.S3Service;
import faang.school.postservice.service.post.cache.PostCacheProcessExecutor;
import faang.school.postservice.service.post.cache.PostCacheService;
import faang.school.postservice.service.post.hash.tag.PostHashTagParser;
import faang.school.postservice.service.redis.CachedAuthorService;
import faang.school.postservice.service.redis.CachedPostService;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static faang.school.postservice.model.VerificationPostStatus.REJECTED;
import static faang.school.postservice.model.VerificationPostStatus.UNVERIFIED;
import static faang.school.postservice.utils.ImageRestrictionRule.POST_IMAGES;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    @Value("${post.images.bucket.name-prefix}")
    private String bucketNamePrefix;

    @Value("${app.post.cache.number_of_top_in_cache}")
    private int numberOfTopInCache;

    @Value("${app.post.subgroup.size}")
    private int subgroupSize;

    private final PostRepository postRepository;
    private final PostValidator postValidator;
    private final SpellingCorrectionService spellingCorrectionService;
    private final S3Service s3Service;
    private final ResourceRepository resourceRepository;
    private final PostHashTagParser postHashTagParser;
    private final PostCacheProcessExecutor postCacheProcessExecutor;
    private final PostMapper postMapper;
    private final PostCacheService postCacheService;
    private final CachedPostService cachedPostService;
    private final CachedAuthorService cachedAuthorService;
    private final UserServiceClient userServiceClient;
    private final KafkaEventProducer kafkaEventProducer;
    private final PostQueryService postQueryService;

    @Transactional
    @SendPostCreatedEvent
    public Post create(Post post) {
        log.info("Create post with id: {}", post.getId());
        postValidator.validateCreatePost(post);

        post.setPublished(false);
        post.setDeleted(false);
        post.setCreatedAt(LocalDateTime.now());
        post.setVerificationStatus(UNVERIFIED);
        postHashTagParser.updateHashTags(post);

        return postRepository.save(post);
    }

    @Transactional
    public Post publish(Long id) {
        log.info("Publish post with id: {}", id);
        Post post = findPostById(id);

        if (post.isPublished()) {
            throw new PostPublishedException(id);
        }

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        postHashTagParser.updateHashTags(post);
        postRepository.save(post);

        cachedPostService.savePostCache(post);
        cachedAuthorService.saveAuthorCache(post.getAuthorId());

        processPostEventAsync(post);

        return post;
    }

    @Async
    public void processPostEventAsync(Post post) {
        getFollowersIds(post)
                .thenAccept(followerIds -> {
                    PostEvent postEvent = new PostEvent(post.getId(), followerIds, post.getPublishedAt());
                    kafkaEventProducer.sendPostEventWithSubgroups(postEvent, subgroupSize);
                    log.info("Post event sent for post ID {}", post.getId());
                })
                .exceptionally(ex -> {
                    log.error("Error processing post event for post ID {}: {}", post.getId(), ex.getMessage());
                    return null;
                });
    }

    @Async
    public CompletableFuture<List<Long>> getFollowersIds(Post post) {
        return CompletableFuture.supplyAsync(() -> {
            UserNewsFeedDto author = userServiceClient.getUserWithFollowers(post.getAuthorId());
            return author.getFollowers();
        });
    }


    @Transactional
    public Post update(Post updatePost) {
        log.info("Update post with id: {}", updatePost.getId());
        Post post = findPostById(updatePost.getId());
        List<String> primalTags = new ArrayList<>(post.getHashTags());

        post.setContent(updatePost.getContent());
        post.setUpdatedAt(LocalDateTime.now());
        postHashTagParser.updateHashTags(post);

        postRepository.save(post);

        if (!post.isDeleted() && post.isPublished()) {
            postCacheProcessExecutor.executeUpdatePostProcess(postMapper.toPostCacheDto(post), primalTags);
        }
        return post;
    }

    @Transactional
    public void delete(Long id) {
        log.info("Delete post with id: {}", id);
        Post post = findPostById(id);

        post.setDeleted(true);
        post.setUpdatedAt(LocalDateTime.now());
        postCacheProcessExecutor.executeDeletePostProcess(postMapper.toPostCacheDto(post), post.getHashTags());

        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public List<PostCacheDto> findInRangeByHashTag(String hashTag, int start, int end) {
        List<PostCacheDto> postDtos = postCacheService.findInRangeByHashTag(hashTag, start, end);

        if (postDtos.isEmpty() && postCacheService.isRedisConnected()) {
            String jsonTag = postHashTagParser.convertTagToJson(hashTag);
            List<Post> posts = postRepository.findTopByHashTagByDate(jsonTag, numberOfTopInCache);
            List<PostCacheDto> postDtosByTop = postMapper.mapToPostCacheDtos(posts);
            postDtos = postDtosByTop.subList(start, Math.min(postDtosByTop.size(), end));

            postCacheProcessExecutor.executeAddListOfPostsToCache(postDtosByTop, hashTag);
        } else if (postDtos.isEmpty()) {
            String jsonTag = postHashTagParser.convertTagToJson(hashTag);
            List<Post> posts = postRepository.findInRangeByHashTagByDate(jsonTag, start, end);
            postDtos = postMapper.mapToPostCacheDtos(posts);
        }
        return postDtos;
    }

    @Transactional(readOnly = true)
    public Post findPostById(Long id) {
        return postQueryService.findPostById(id);
    }

    @Transactional(readOnly = true)
    @SendPostViewEventToAnalytics(Post.class)
    public Post get(Long postId) {
        return findPostById(postId);
    }

    @Transactional(readOnly = true)
    @SendPostViewEventToAnalytics(List.class)
    public List<Post> searchByAuthor(Post filterPost) {
        List<Post> posts = postRepository.findByAuthorId(filterPost.getAuthorId());
        posts = applyFiltersAndSorted(posts, filterPost)
                .toList();
        return posts;
    }

    @Transactional(readOnly = true)
    @SendPostViewEventToAnalytics(List.class)
    public List<Post> searchByProject(Post filterPost) {
        List<Post> posts = postRepository.findByProjectId(filterPost.getProjectId());
        posts = applyFiltersAndSorted(posts, filterPost)
                .toList();

        return posts;
    }

    @Transactional(readOnly = true)
    public List<Long> findUserIdsForBan() {
        return postRepository.findAllUsersBorBan(REJECTED);
    }

    public void correctPosts(List<Post> draftPosts) {
        draftPosts.forEach(post -> {
            try {
                String correctedContent = spellingCorrectionService.getCorrectedContent(post.getContent());

                LocalDateTime currentTime = LocalDateTime.now();
                post.setContent(correctedContent);
                post.setUpdatedAt(currentTime);
                post.setScheduledAt(currentTime);
            } catch (RepeatableServiceException exception) {
                log.error("Контент поста {} не прошёл авто корректировку, после переотправок", post.getId());
            } catch (DontRepeatableServiceException exception) {
                log.error("Контент поста {} не прошёл авто корректировку из-за ошибки сервиса", post.getId());
            }
        });

        postRepository.saveAll(draftPosts);
    }

    private Stream<Post> applyFiltersAndSorted(List<Post> posts, Post filterPost) {
        return posts.stream()
                .filter((post -> !post.isDeleted()))
                .filter((post -> post.isPublished() == filterPost.isPublished()))
                .sorted(Comparator.comparing(
                        filterPost.isPublished() ? Post::getPublishedAt : Post::getCreatedAt
                ).reversed());
    }

    @Transactional
    public void uploadImages(Long postId, List<MultipartFile> images) {
        log.info("Upload images to post {}", postId);
        postValidator.validateImagesToUpload(postId, images);

        List<Resource> resourcesToSave = new ArrayList<>();

        Post post = findPostById(postId);
        String folder = bucketNamePrefix + post.getId();
        images.forEach(image -> {
            try {
                Resource uploadedImage = s3Service.uploadFile(image, folder, POST_IMAGES);
                uploadedImage.setPost(post);
                resourcesToSave.add(uploadedImage);
                log.info("Image {} was uploaded to S3", uploadedImage.getId());
            } catch (Exception e) {
                log.error("Failed to upload image {} to S3", image.getOriginalFilename(), e);
                throw new UploadImageToPostException(image.getOriginalFilename(), postId);
            }
        });

        resourceRepository.saveAll(resourcesToSave);
        log.info("Images successfully uploaded to post {}", postId);
    }

    @Transactional(readOnly = true)
    public Resource findResourceById(Long resourceId) {
        return resourceRepository.findById(resourceId).orElseThrow(() -> new ResourceNotFoundException(resourceId));
    }

    public org.springframework.core.io.Resource downloadImage(Resource resource) {
        log.info("Download image {}", resource.getId());
        try (InputStream inputStream = s3Service.downloadFile(resource.getKey())) {
            return new InputStreamResource(inputStream);
        } catch (Exception e) {
            log.error("Failed to download image {}", resource.getId(), e);
            throw new DownloadImageFromPostException(resource.getId());
        }
    }

    @Transactional
    public void deleteImagesFromPost(List<Long> resourceIds) {
        log.info("Delete images");
        List<Resource> resources = resourceRepository.findAllByIdIn(resourceIds);

        List<String> keys = resources.stream().map(Resource::getKey).toList();
        s3Service.deleteFiles(keys);

        resourceRepository.deleteAll(resources);
        log.info("Images successfully deleted");
    }

    @Transactional(readOnly = true)
    public List<Long> getReadyToPublishIds() {
        return postRepository.findReadyToPublishIds();
    }

    @Async("postExecutorPool")
    @Transactional
    public void processReadyToPublishPosts(List<Long> postIds) {
        List<Post> postList = postRepository.findPostsByIds(postIds);
        LocalDateTime currentDateTime = LocalDateTime.now();
        postList.forEach(post -> {
            post.setPublishedAt(currentDateTime);
            post.setPublished(true);
        });
        postRepository.saveAll(postList);
        log.info("Posts was published by scheduling: {}", postIds);
    }
}
