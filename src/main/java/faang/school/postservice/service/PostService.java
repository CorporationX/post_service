package faang.school.postservice.service;

import faang.school.postservice.client.LanguageToolClient;
import faang.school.postservice.dto.languageTool.GrammarMatch;
import faang.school.postservice.dto.languageTool.LanguageToolResponseDto;
import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.exception.InvalidFileException;
import faang.school.postservice.exception.LanguageToolException;
import faang.school.postservice.exception.MaxResourcesReachedException;
import faang.school.postservice.exception.PostIdMismatchException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.exception.not_found_exceptions.ResourceNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.ResourceMapper;
import faang.school.postservice.messages.ExceptionMessages;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.PostResourceRepository;
import faang.school.postservice.service.hashtags.HashtagService;
import faang.school.postservice.utils.validationUtils.PostValidation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static faang.school.postservice.messages.ValidationMessages.VALIDATION_CONTENT_TYPE;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {
    public static final String CANT_UPDATE_DELETED_POST = "Can't update deleted post";
    public static final String NO_POST_FOUND = "No post found with ID %d";
    public static final String POST_HAS_ALREADY_BEEN_DELETED = "Post has already been deleted";
    private static final int TIMEOUT_HOURS = 2;
    private static final Long BYTES_FILE_SIZE = 5L * 1024L * 1024L;
    private static final Integer MAX_COUNT_OF_RESOURCES = 10;
    private static final int STANDARD_WIDTH = 1080;
    private static final int HORIZONTAL_HEIGHT = 566;
    private static final int VERTICAL_SQUARE_HEIGHT = 1080;

    private final ResourceMapper resourceMapper;
    private final PostMapper postMapper;
    private final LanguageToolClient languageToolClient;
    private final LikeRepository likeRepository;
    private final PostResourceRepository postResourceRepository;
    private final PostRepository postRepository;
    private final HashtagService hashtagService;
    private final MinioService minioService;

    @Value("${posts.correction.batch-size}")
    int batchSize;

    @Value("${posts.correction.thread-poop-size}")
    int threadPoolSize;

    public PostResponseDto createDraftPost(PostRequestDto postRequestDto) {
        PostValidation.validatePostAuthors(postRequestDto);
        PostValidation.validatePostDraftCreation(postRequestDto);
        Post post = postRepository.save(postMapper.toPost(postRequestDto));
        return postMapper.toPostResponseDto(post);
    }

    public PostResponseDto publishPost(Long postId) {
        Optional<Post> postDraftOptional = postRepository.findById(postId);
        validatePostOptional(postDraftOptional, postId);
        Post post = postDraftOptional.get();
        PostValidation.validatePostInPublishing(post);

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        postRepository.save(post);
        hashtagService.extractHashtagsFromPost(post);
        return postMapper.toPostResponseDto(post);
    }

    public PostResponseDto updatePost(PostRequestDto postRequestDto) {
        PostValidation.validatePostUpdate(postRequestDto);
        Optional<Post> postOptional = postRepository.findById(postRequestDto.getId());
        validatePostOptional(postOptional, postRequestDto.getId());
        Post post = postOptional.get();

        if (post.isDeleted()) {
            log.error(CANT_UPDATE_DELETED_POST);
            throw new IllegalArgumentException(CANT_UPDATE_DELETED_POST);
        }
        int updateCounter = 0;
        if (!postRequestDto.getContent().equals(post.getContent())) {
            updateCounter++;
            post.setContent(postRequestDto.getContent());
        }
        if (updateCounter == 0) {
            log.warn("Nothing was updated for post with ID {}", postRequestDto.getId());
        }
        postRepository.save(post);
        return postMapper.toPostResponseDto(post);
    }

    public PostResponseDto deletePost(Long postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        validatePostOptional(postOptional, postId);

        Post post = postOptional.get();
        if (post.isDeleted()) {
            log.error(POST_HAS_ALREADY_BEEN_DELETED);
            throw new IllegalArgumentException(POST_HAS_ALREADY_BEEN_DELETED);
        }
        post.setDeleted(true);
        postRepository.save(post);
        return postMapper.toPostResponseDto(post);
    }

    public PostResponseDto getPostById(Long postId) {
        PostValidation.validatePostId(postId);
        Optional<Post> optionalPost = postRepository.findById(postId);
        validatePostOptional(optionalPost, postId);

        return postMapper.toPostResponseDto(optionalPost.get());
    }

    public List<PostResponseDto> getUserDraftPosts(Long userId) {
        PostValidation.validateUserId(userId);
        List<Post> drafts = postRepository.findDraftsByAuthorId(userId);
        return postMapper.toPostResponseDtoList(drafts);
    }

    public List<PostResponseDto> getProjectDraftPosts(Long projectId) {
        PostValidation.validateProjectId(projectId);
        List<Post> drafts = postRepository.findDraftsByProjectId(projectId);
        return postMapper.toPostResponseDtoList(drafts);
    }

    public List<PostResponseDto> getUserPublishedPosts(Long userId) {
        PostValidation.validateUserId(userId);
        List<Post> posts = postRepository.findPublishedByAuthorId(userId);
        return postMapper.toPostResponseDtoList(posts);
    }

    public List<PostResponseDto> getProjectPublishedPosts(Long projectId) {
        PostValidation.validateProjectId(projectId);
        List<Post> posts = postRepository.findPublishedByProjectId(projectId);
        return postMapper.toPostResponseDtoList(posts);
    }

    public void sendPostsForChecking() {
        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        long total = postRepository.count();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int start = 0; start < total; start += batchSize) {
            int end = Math.min(start + batchSize - 1, (int) total);
            int size = end - start + 1;
            int finalStart = start;
            futures.add(CompletableFuture.runAsync(() -> {
                Pageable pageable = PageRequest.of((finalStart + size - 1) / size, size);
                Page<Post> postContents = postRepository.findUncorrectedPosts(pageable);
                postContents.forEach(this::sendPostContentChecking);
            }, executor));
        }

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(TIMEOUT_HOURS, TimeUnit.HOURS);
            log.info("Submitting posts for review completed");
        } catch (TimeoutException e) {
            log.error("Submitting posts for review haven't completed on time");
        } catch (InterruptedException e) {
            log.error("Submitting posts for review was interrupted. ", e);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            log.error("Execution exception while submitting posts for review. ", e);
        } finally {
            executor.shutdown();
        }
    }

    @Retryable(
            retryFor = {LanguageToolException.class},
            maxAttemptsExpression = "${spring.retry.language-tool.max-attempts}",
            backoff = @Backoff(delayExpression = "${spring.retry.language-tool.backoff-delay}")
    )
    @Transactional
    private void sendPostContentChecking(Post post) {
        String text = post.getContent();
        log.debug("Before correcting errors in the text: {}", text);
        LanguageToolResponseDto response = languageToolClient.getCorrectedText(text, "auto");
        post.setContent(response != null ? correctText(text, response) : text);
        post.setCorrected(true);
        postRepository.save(post);
        log.debug("After correcting errors in the text: {}", text);
    }

    private void validatePostOptional(Optional<Post> postOptional, Long id) {
        if (postOptional.isEmpty()) {
            String message = String.format(NO_POST_FOUND, id);
            log.error(message);
            throw new PostNotFoundException(message);
        }
    }

    private String correctText(String text, LanguageToolResponseDto response) {
        if (response.getMatches() == null || response.getMatches().isEmpty()) {
            return text;
        }
        response.getMatches().sort(Comparator.comparingInt(GrammarMatch::getOffset));
        StringBuilder correctedText = new StringBuilder(text);
        int offsetCorrection = 0;

        for (GrammarMatch match : response.getMatches()) {
            int offset = match.getOffset() + offsetCorrection;
            int length = match.getLength();
            String replacement = match.getReplacements().isEmpty() ? ""
                    : match.getReplacements().get(0).getValue();

            offsetCorrection += replacement.length() - length;
            correctedText.replace(offset, offset + length, replacement);
        }
        return correctedText.toString();
    }

    @Recover
    private void recoverSendPostContentChecking(LanguageToolException e, Post post) {
        log.error("Failed to correct text after retries. ", e);
        log.error("Post with ID could not be corrected: {}", post.getId());
    }

    @Transactional
    public List<ResourceDto> add(Long postId, List<MultipartFile> files) {
        files.forEach(this::validateFile);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new faang.school.postservice.exception.not_found_exceptions.PostNotFoundException(ExceptionMessages.POST_NOT_FOUND_EXCEPTION));

        if (post.getResources().size() == MAX_COUNT_OF_RESOURCES) {
            log.error(ExceptionMessages.RESOURCE_MAX_LIMIT_EXCEPTION);
            throw new MaxResourcesReachedException(ExceptionMessages.RESOURCE_MAX_LIMIT_EXCEPTION);
        }

        List<Resource> uploadedResource = new ArrayList<>();

        for (MultipartFile file : files) {
            String key = file.getOriginalFilename() + System.currentTimeMillis();
            Resource resource = processAndUploadFile(file, key);

            resource.setPost(post);
            post.getResources().add(resource);
            uploadedResource.add(resource);
        }

        log.info("The file was added to the post");
        return resourceMapper.toResourceDtoList(uploadedResource);
    }

    public void delete(Long postId, Long resourceId) {
        Resource resource = postResourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.RESOURCE_NOT_FOUND_EXCEPTION));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new faang.school.postservice.exception.not_found_exceptions.PostNotFoundException(ExceptionMessages.POST_NOT_FOUND_EXCEPTION));
        String key = resource.getKey();

        if (!postId.equals(resource.getPost().getId())) {
            log.error(ExceptionMessages.POST_ID_MISMATCH_EXCEPTION);
            throw new PostIdMismatchException(
                    ExceptionMessages.POST_ID_MISMATCH_EXCEPTION
            );
        }

        post.getResources().remove(resource);
        postResourceRepository.deleteById(resourceId);
        postRepository.save(post);
        minioService.delete(key);
        log.info("The file has been removed from the bucket");
    }

    private Resource processAndUploadFile(MultipartFile file, String key) {
        String contentType = file.getContentType();

        if (contentType == null) {
            log.error(VALIDATION_CONTENT_TYPE);
            throw new InvalidFileException(VALIDATION_CONTENT_TYPE);
        }

        if (file.getOriginalFilename() == null) {
            log.error(ExceptionMessages.FILE_ORIGINAL_NAME_EMPTY_EXCEPTION);
            throw new InvalidFileException(ExceptionMessages.FILE_ORIGINAL_NAME_EMPTY_EXCEPTION);
        }

        String fileExtension = file.getOriginalFilename().
                substring(file.getOriginalFilename().lastIndexOf(".") + 1);

        if (contentType.startsWith("image")) {
            try {
                BufferedImage croppedImage = resizeImage(file);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(croppedImage, fileExtension, outputStream);
                byte[] imageBytes = outputStream.toByteArray();

                try (InputStream inputStream = new ByteArrayInputStream(imageBytes)) {
                    return minioService.uploadImage(inputStream, imageBytes, key, file.getName(), contentType);
                }
            } catch (IOException e) {
                throw new InvalidFileException(ExceptionMessages.FILE_PROCESS_IMAGE_EXCEPTION);
            }
        }

        if (contentType.startsWith("video") || contentType.startsWith("audio")) {
            return minioService.uploadVideoOrAudio(file, key);
        } else {
            throw new InvalidFileException("Unsupported file type: " + contentType);
        }
    }

    private BufferedImage resizeImage(MultipartFile file) throws IOException {
        BufferedImage image = ImageIO.read(file.getInputStream());

        boolean isHorizontal = image.getWidth() > image.getHeight();

        int imageWidth = STANDARD_WIDTH;
        int imageHeight = isHorizontal ? HORIZONTAL_HEIGHT : VERTICAL_SQUARE_HEIGHT;

        if (image.getWidth() <= imageWidth && image.getHeight() <= imageHeight) {
            return image;
        }

        return Thumbnails.of(image)
                .size(imageWidth, imageHeight)
                .keepAspectRatio(false)
                .asBufferedImage();
    }

    private void validateFile(MultipartFile file) {
        if (file.getName().isBlank()) {
            log.error(ExceptionMessages.FILE_NAME_EMPTY_EXCEPTION);
            throw new InvalidFileException(ExceptionMessages.FILE_NAME_EMPTY_EXCEPTION);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            log.error(ExceptionMessages.FILE_ORIGINAL_NAME_EMPTY_EXCEPTION);
            throw new InvalidFileException(ExceptionMessages.FILE_ORIGINAL_NAME_EMPTY_EXCEPTION);
        }

        if (file.getSize() > BYTES_FILE_SIZE) {
            log.error(ExceptionMessages.FILE_SIZE_EXCEPTION);
            throw new InvalidFileException(ExceptionMessages.FILE_SIZE_EXCEPTION);
        }
    }
}
