package faang.school.postservice.service.post;

import faang.school.postservice.exception.ResourceNotFoundException;
import faang.school.postservice.exception.post.PostNotFoundException;
import faang.school.postservice.exception.post.PostPublishedException;
import faang.school.postservice.exception.spelling_corrector.DontRepeatableServiceException;
import faang.school.postservice.exception.spelling_corrector.RepeatableServiceException;
import faang.school.postservice.exception.post.image.DownloadImageFromPostException;
import faang.school.postservice.exception.post.image.UploadImageToPostException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.aws.s3.S3Service;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static faang.school.postservice.utils.ImageRestrictionRule.POST_IMAGES;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    @Value("${post.images.bucket.name-prefix}")
    private String bucketNamePrefix;

    private final PostRepository postRepository;
    private final PostValidator postValidator;
    private final SpellingCorrectionService spellingCorrectionService;
    private final S3Service s3Service;
    private final ResourceRepository resourceRepository;

    @Transactional
    public Post create(Post post) {
        postValidator.validateCreatePost(post);

        post.setPublished(false);
        post.setDeleted(false);
        post.setCreatedAt(LocalDateTime.now());

        postRepository.save(post);

        return post;
    }

    @Transactional
    public Post update(Post updatePost) {
        Post post = findPostById(updatePost.getId());

        post.setContent(updatePost.getContent());
        post.setUpdatedAt(LocalDateTime.now());

        postRepository.save(post);

        return post;
    }

    @Transactional
    public Post publish(Long id) {
        Post post = findPostById(id);

        if (post.isPublished()) {
            throw new PostPublishedException(id);
        }

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        postRepository.save(post);

        return post;
    }

    @Transactional
    public void delete(Long id) {
        Post post = findPostById(id);

        post.setDeleted(true);
        post.setUpdatedAt(LocalDateTime.now());

        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public Post findPostById(Long id) {
        return postRepository.findByIdAndNotDeleted(id).orElseThrow(() -> new PostNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Post> searchByAuthor(Post filterPost) {
        List<Post> posts = postRepository.findByAuthorId(filterPost.getAuthorId());
        posts = applyFiltersAndSorted(posts, filterPost)
                .toList();

        return posts;
    }

    @Transactional(readOnly = true)
    public List<Post> searchByProject(Post filterPost) {
        List<Post> posts = postRepository.findByProjectId(filterPost.getProjectId());
        posts = applyFiltersAndSorted(posts, filterPost)
                .toList();

        return posts;
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
}
