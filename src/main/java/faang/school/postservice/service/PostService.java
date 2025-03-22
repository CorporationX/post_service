package faang.school.postservice.service;

import faang.school.postservice.client.SpellingCheckClient;
import faang.school.postservice.dto.Post.CreatePostDraftDto;
import faang.school.postservice.dto.Post.PostResponseDto;
import faang.school.postservice.dto.Post.UpdatePostDto;
import faang.school.postservice.dto.spellcheck.AiTextRequestDto;
import faang.school.postservice.dto.spellcheck.AiTextResponseDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostCorrectionValidator;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final KafkaTemplate<String, Long> kafkaTemplate;
    private final PostMapper postMapper;
    private final PostValidator postValidator;
    private final ResourseService resourseService;
    private final SpellingCheckClient spellingCheckClient;
    private final PostCorrectionValidator postCorrectionValidator;

    @Value("${author.banner.rejected_posts_to_ban}")
    private int rejectedPostsToBan;
    @Value("${author.banner.kafka_topic}")
    private String banTopic;

    public PostResponseDto createDraft(CreatePostDraftDto postDraftDto) {
        Post post = postMapper.fromCreateDto(postDraftDto);
        postValidator.validatePostAuthorExist(post);
        postValidator.validatePostDraftInfo(post);
        Post savedPost = postRepository.save(post);
        return postMapper.toResponseDto(savedPost);
    }

    @Transactional
    public PostResponseDto publishPost(long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));
        postValidator.validateNotPublished(post);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        Post savedPost = postRepository.save(post);
        return postMapper.toResponseDto(savedPost);
    }

    public PostResponseDto updatePost(UpdatePostDto postDto) {
        Post post = postRepository.findById(postDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postDto.getId()));
        post = postMapper.update(post, postDto);
        postValidator.validatePostDraftInfo(post);
        Post savedPost = postRepository.save(post);
        return postMapper.toResponseDto(savedPost);
    }

    public PostResponseDto safeDeletePost(long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));
        postValidator.validateNotDeleted(post);
        post.setDeleted(true);
        Post savedPost = postRepository.save(post);
        return postMapper.toResponseDto(savedPost);
    }

    public PostResponseDto getPost(long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));
        return postMapper.toResponseDto(post);
    }

    @Transactional(readOnly = true)
    public Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));
    }

    public List<PostResponseDto> getUserDrafts(long userId) {
        return getExistingPostsSortedByDate(
                postRepository::findByAuthorId,
                Post::getCreatedAt,
                userId, false
        );
    }

    public List<PostResponseDto> getProjectDrafts(long projectId) {
        return getExistingPostsSortedByDate(
                postRepository::findByProjectId,
                Post::getCreatedAt,
                projectId, false
        );
    }

    public List<PostResponseDto> getUserPosts(long userId) {
        return getExistingPostsSortedByDate(
                postRepository::findByAuthorIdWithLikes,
                Post::getPublishedAt,
                userId, true
        );
    }

    public List<PostResponseDto> getProjectPosts(long projectId) {
        return getExistingPostsSortedByDate(
                postRepository::findByProjectIdWithLikes,
                Post::getPublishedAt,
                projectId, true
        );
    }

    @Transactional(readOnly = true)
    public void postAuthorsToBan() {
        List<Long> authorIdsToBan = findAuthorIdsToBan();
        log.info("Start publishing authors to ban");
        for (Long authorIdToBan : authorIdsToBan) {
            log.debug("Publishing author {} to ban", authorIdToBan);
            kafkaTemplate.send(banTopic, authorIdToBan);
        }
        log.info("Finish publishing authors to ban");
    }

    public void uploadImages(Long postId, List<MultipartFile> files) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Post not found"));
        for (MultipartFile file : files) {
            resourseService.addResource(post, file);
        }
    }

    private List<Long> findAuthorIdsToBan() {
        log.info("Start search authors to ban.");
        List<Long> authorIdsForBan = postRepository.findAuthorsForBan(rejectedPostsToBan);
        log.info("End search authors to ban. Found {} authors", authorIdsForBan);
        return authorIdsForBan;
    }

    private List<PostResponseDto> getExistingPostsSortedByDate(
            Function<Long, List<Post>> repositoryMethod,
            Function<Post, LocalDateTime> fieldToSortBy,
            Long id, boolean published) {
        return repositoryMethod.apply(id).stream()
                .filter(post -> post.isPublished() == published && !post.isDeleted())
                .sorted(Comparator.comparing(fieldToSortBy).reversed())
                .map(postMapper::toResponseDto)
                .toList();
    }

    @Transactional
    public void correctUnpublishedPosts() {
        List<Post> posts = postRepository.findAllByPublishedFalse();

        if (posts.isEmpty()) {
            log.info("No unpublished posts found for correction.");
            return;
        }

        log.info("Found {} unpublished posts for spell check.", posts.size());

        for (Post post : posts) {
            if (!postCorrectionValidator.isTextValid(post.getContent())) {
                log.warn("Skipping post with ID {}: invalid or empty content.", post.getId());
                continue;
            }

            String correctedContent;
            try {
                correctedContent = getCorrectedContentWithRetry(post.getContent());
            } catch (Exception ex) {
                log.error("Spell check failed for post ID {} after retries. Reason: {}", post.getId(), ex.getMessage());
                continue;
            }

            if (!postCorrectionValidator.isCorrectionValid(correctedContent)) {
                log.warn("Skipping post with ID {}: corrected content is invalid.", post.getId());
                continue;
            }

            if (!postCorrectionValidator.isCorrectionDifferent(post.getContent(), correctedContent)) {
                log.debug("Skipping post with ID {}: no correction needed.", post.getId());
                continue;
            }

            post.setContent(correctedContent);
            postRepository.save(post);
            log.info("Post with ID {} successfully updated with corrected content.", post.getId());
        }

        log.info("Spell correction process finished.");
    }

    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public String getCorrectedContentWithRetry(String content) {
        AiTextRequestDto request = new AiTextRequestDto(content);
        AiTextResponseDto response = spellingCheckClient.checkText(request);
        return response.getCorrected();
    }

    @Recover
    public String recoverFromSpellCheckFailure(Exception ex, String content) {
        log.error("All retries failed for content '{}'. Returning original content.", content);
        return content;
    }
}
