package faang.school.postservice.service;

import faang.school.postservice.dto.AuthorPostCount;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.event.EventsGenerator;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.utils.PostSpecifications;
import faang.school.postservice.validator.PostServiceValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final PostServiceValidator<PostDto> validator;

    private final PostCacheService postCacheService;
    private final EventsGenerator eventsGenerator;
    private final AuthorCacheService authorCacheService;
    private final ExecutorService executorService;
    private final RedisTemplate<String, Object> redisTemplate;


    @Value("${spring.data.redis.channel.user-bans-channel}")
    private String userBansChannelName;
    private final PostVerificationService postVerificationService;

    @Value("${ad.batch.size}")
    private int batchSize;

    public PostDto createPost(final PostDto postDto) {
        validator.validate(postDto);

        Post post = postMapper.toEntity(postDto);

        return postMapper.toDto(postRepository.save(post));
    }

    public PostDto publishPost(final long postId) {
        Post post = getPostByIdOrFail(postId);

        validatePostPublishing(post);

        LocalDateTime now = LocalDateTime.now();
        post.setPublished(true);
        post.setPublishedAt(now);
        post.setUpdatedAt(now);

        var savedPost = postRepository.save(post);
        var postDto = postMapper.toDto(savedPost);

        authorCacheService.saveAuthorCache(postDto.getAuthorId());
        postCacheService.savePostCache(postDto);
        eventsGenerator.generateAndSendPostFollowersEvent(postDto);

        return postDto;
    }


    public PostDto updatePost(final long postId, final PostDto postDto) {
        Post newPost = postMapper.toEntity(postDto);
        Post post = getPostByIdOrFail(postId);

        post.setContent(newPost.getContent());
        post.setUpdatedAt(LocalDateTime.now());

        return postMapper.toDto(postRepository.save(post));
    }


    public void deletePost(final long postId) {
        Post post = getPostByIdOrFail(postId);

        post.setDeleted(true);
        post.setUpdatedAt(LocalDateTime.now());

        postRepository.save(post);
    }

    public PostDto getPost(final long postId) {
        Post post = getPostByIdOrFail(postId);
        var postDto = postMapper.toDto(post);

        eventsGenerator.generateAndSendPostViewEvent(postDto);
        return postDto;
    }

    public List<PostDto> getPostsByIds(List<Long> postIds) {
        return postRepository.findAllById(postIds).stream()
                .map(postMapper::toDto)
                .toList();
    }

    @Transactional
    public void checkAndVerifyPosts() {
        List<Post> postsToVerify = postRepository.findAllByVerifiedDateIsNull(PostSpecifications.isReadyToPublish());
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < postsToVerify.size(); i += batchSize) {
            int end = Math.min(i + batchSize, postsToVerify.size());
            List<Post> batch = postsToVerify.subList(i, end);

            CompletableFuture<Void> future = postVerificationService.checkAndVerifyPostsInBatch(batch);
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private List<AuthorPostCount> getUnverifiedPostsGroupedByAuthor() {
        List<Object[]> rawResults = postRepository.findUnverifiedPostsGroupedByAuthor();

        return rawResults.stream()
                .map(result -> new AuthorPostCount((Long) result[0], (Long) result[1]))
                .collect(Collectors.toList());
    }

    public void banOffensiveAuthors() {
        List<AuthorPostCount> unverifiedPostsByAuthor = getUnverifiedPostsGroupedByAuthor();

        List<AuthorPostCount> offensiveAuthors = unverifiedPostsByAuthor.stream()
                .filter(entry -> entry.getPostCount() > 5)
                .toList();

        log.info("Found {} authors with more than 5 unverified posts", offensiveAuthors.size());

        offensiveAuthors.forEach(entry -> {
            Long authorId = entry.getAuthorId();
            redisTemplate.convertAndSend(userBansChannelName, authorId);
        });
    }

    public void publishScheduledPosts() {
        //List<Post> postsToPublish = postRepository.findAll(PostSpecifications.isReadyToPublish());
        List<Post> postsToPublish = postRepository.findAllByVerifiedDateIsNull(PostSpecifications.isReadyToPublish());

        if (postsToPublish.isEmpty()) {
            log.info("No posts to publish at this time");
            return;
        }

        log.info("Starting batch processing for {} posts", postsToPublish.size());

        for (int i = 0; i < postsToPublish.size(); i += batchSize) {
            int end = Math.min(i + batchSize, postsToPublish.size());
            List<Post> batch = postsToPublish.subList(i, end);

            CompletableFuture.runAsync(() -> {
                log.info("Processing batch of size {}", batch.size());
                try {
                    LocalDateTime now = LocalDateTime.now();
                    for (Post post : batch) {
                        post.setPublished(true);
                        post.setPublishedAt(now);
                    }
                    postRepository.saveAll(batch);
                    log.info("Successfully saved batch of size {}", batch.size());
                } catch (Exception e) {
                    log.error("Error while processing batch: {}", batch, e);
                }
            }, executorService);
        }
        log.info("Batch processing completed");
    }

    public List<PostDto> getFilteredPosts(final Long authorId, final Long projectId, final Boolean isPostPublished) {
        List<Post> result = new ArrayList<>();
        boolean isPublished = isPostPublished;

        if (authorId != null) {
            result = postRepository.findByAuthorIdAndPublishedAndDeletedIsFalseOrderByPublished(authorId, isPublished);
        } else if (projectId != null) {
            result = postRepository.findByProjectIdAndPublishedAndDeletedIsFalseOrderByPublished(projectId, isPublished);
        }

        return result.stream()
                .map((postMapper::toDto))
                .toList();
    }

    public void validatePostPublishing(Post post) {
        if (post.isPublished()) {
            throw new IllegalArgumentException("Post is already published");
        }
    }

    public Post getPostByIdOrFail(long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
    }
}