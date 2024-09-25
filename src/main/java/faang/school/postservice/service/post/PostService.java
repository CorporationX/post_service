package faang.school.postservice.service.post;

import faang.school.postservice.exception.ResourceNotFoundException;
import faang.school.postservice.exception.ValidationException;
import faang.school.postservice.exception.post.PostNotFoundException;
import faang.school.postservice.exception.post.PostPublishedException;
import faang.school.postservice.exception.post.image.DownloadImageToPostException;
import faang.school.postservice.exception.post.image.UploadImageToPostException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.aws.s3.S3Service;
import faang.school.postservice.utils.ImageProcessingUtils;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    @Value("${post.images.max-to-upload}")
    private int imagesMaxNumber;

    @Value("${post.images.bucket.name-prfix}")
    private String bucketNamePrefix;

    private final PostRepository postRepository;
    private final PostValidator postValidator;
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
        validateImagesToUpload(postId, images);

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

    private void validateImagesToUpload(Long postId, List<MultipartFile> images) {
        if (isEmpty(images)) {
            throw new ValidationException("List of images to upload to post %s cannot be null or empty", postId);
        }

        if (images.size() > imagesMaxNumber) {
            throw new ValidationException("Max number of images to upload to post is %s", imagesMaxNumber);
        }

        List<Resource> existedImages = resourceRepository.findAllByPostId(postId);
        if (existedImages.size() + images.size() > imagesMaxNumber) {
            throw new ValidationException("Post %s already has %s images. You cannot add %s more. Max size is %s",
                    postId, existedImages.size(), images.size(), imagesMaxNumber);
        }

        images.forEach(image -> validateImageToUpload(postId, image));
    }

    private void validateImageToUpload(Long postId, MultipartFile image) {
        String imageName = image.getOriginalFilename();
        if (image.getSize() > POST_IMAGES.getMaxSize()) {
            throw new ValidationException("You cannot upload file %s to post %s with size %s. Max size is %s",
                    imageName, postId, image.getSize(), POST_IMAGES.getMaxSize());
        }

        String contentType = image.getContentType();
        if (contentType == null) {
            throw new ValidationException("You cannot upload file %s to post %s without contentType.", imageName, postId);
        }

        List<String> availableImageTypes = ImageProcessingUtils.getAvailableImageTypes();
        if (availableImageTypes.stream().noneMatch(type -> type.equals(contentType))) {
            throw new ValidationException("You cannot upload file %s to post %s with %s contentType", imageName, postId, contentType);
        }
    }

    @Transactional(readOnly = true)
    public Resource findResourceById(Long resourceId) {
        return resourceRepository.findById(resourceId).orElseThrow(() -> new ResourceNotFoundException(resourceId));
    }

    public byte[] downloadImage(Resource resource) {
        log.info("Download image {}", resource.getId());
        try {
            InputStream inputStream = s3Service.downloadFile(resource.getKey());
            return inputStream.readAllBytes();
        } catch (Exception e) {
            log.error("Failed to download image {}", resource.getId(), e);
            throw new DownloadImageToPostException(resource.getId());
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
