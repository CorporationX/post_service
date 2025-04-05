package faang.school.postservice.service.impl;

import faang.school.postservice.broker.producer.PostEventProducer;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.config.feed.NewsFeedProperties;
import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.dto.feed.FeedItemCommentDto;
import faang.school.postservice.dto.post.PostCommentEvent;
import faang.school.postservice.dto.post.PostCreateRequestDto;
import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.PostUpdateRequestDto;
import faang.school.postservice.exception.DataFetchException;
import faang.school.postservice.filter.post.PostSpecificationFilter;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    @Value("${post_service.batch_size}")
    private int postBatchSize;

    private final PostRepository postRepository;
    private final PostServiceValidator postServiceValidator;
    private final PostMapper postMapper;
    private final List<PostSpecificationFilter> postSpecificationFilters;
    private final ExecutorService executorService;
    private final PostEventProducer postEventProducer;
    private final RedisTemplate<String, PostResponseDto> postRedisTemplate;
    private final RedisProperties redisProperties;
    private final UserContext userContext;
    private final CommentMapper commentMapper;
    private final NewsFeedProperties newsFeedProperties;

    @Override
    @Transactional
    public PostResponseDto createPostDraft(PostCreateRequestDto postCreateRequestDto) {
        postServiceValidator.validatePostDto(postCreateRequestDto);
        Post post = postMapper.toPostEntity(postCreateRequestDto);
        Post draftPost = postRepository.save(post);
        log.info("Post draft created, id = {}", draftPost.getId());
        return postMapper.toPostResponseDto(draftPost);
    }

    @Override
    @Transactional
    public PostResponseDto publishPostDraft(Long postId) {
        Post draftPostToPublish = getPostById(postId);
        postServiceValidator.validatePostBeforePublish(draftPostToPublish);
        draftPostToPublish.setPublished(true);
        draftPostToPublish.setPublishedAt(LocalDateTime.now());
        Post publishedPost = postRepository.save(draftPostToPublish);
        log.info("Draft post is published, id = {}", publishedPost.getId());

        String cacheKey = redisProperties.cache().postCacheName() + postId;
        PostResponseDto postResponseDto = postMapper.toPostResponseDto(publishedPost);
        postRedisTemplate.opsForValue().set(
                cacheKey,
                postResponseDto,
                redisProperties.cache().postTtlMinutes(),
                TimeUnit.MINUTES
        );
        postEventProducer.producePublishPostEventAsync(userContext.getUserId(), publishedPost);
        return postMapper.toPostResponseDto(publishedPost);
    }

    @Override
    @Transactional
    public void publishScheduledPosts() {
        // TODO переделать логику, чтобы по ним также заполнялся feed
        PostFilterDto postFilterDto = PostFilterDto.builder()
                .isPublished(false)
                .isDeleted(false)
                .shouldBePublishedBefore(LocalDateTime.now())
                .build();

        List<Post> scheduledPosts = findAllPostsByFilter(postFilterDto);
        List<List<Post>> postBatches = ListUtils.partition(scheduledPosts, postBatchSize);
        postBatches.stream()
                .map(this::preparePostList)
                .map(postsBatch -> CompletableFuture.runAsync(() -> {
                            postRepository.saveAll(postsBatch);
                        }, executorService)
                        .exceptionally(error -> {
                            log.error("Error processing scheduled posts", error);
                            throw new RuntimeException("Failed to process scheduled posts", error);
                        })).forEach(CompletableFuture::join);
    }

    @Override
    @Transactional
    public PostResponseDto updatePost(Long postId, PostUpdateRequestDto postUpdateRequestDto) {
        Post postToUpdate = getPostById(postId);
        Post requestPost = postMapper.toPostEntity(postUpdateRequestDto);
        Post updatedPost = postRepository.save(copyPostData(requestPost, postToUpdate));

        String cacheKey = redisProperties.cache().postCacheName() + postId;
        PostResponseDto postResponseDto = postMapper.toPostResponseDto(updatedPost);
        postRedisTemplate.opsForValue().set(
                cacheKey,
                postResponseDto,
                redisProperties.cache().postTtlMinutes(),
                TimeUnit.MINUTES
        );

        log.info("Post is updated, id = {}", updatedPost.getId());
        return postMapper.toPostResponseDto(updatedPost);
    }

    @Override
    @Transactional
    public void deletePost(Long postId) {
        Post postToDelete = getPostById(postId);
        postToDelete.setDeleted(true);
        log.info("Post is deleted, id = {}", postToDelete.getId());
        postRepository.save(postToDelete);

        String cacheKey = redisProperties.cache().postCacheName() + postId;
        postRedisTemplate.delete(cacheKey);
        log.info("Evicted cache for post id: {}", postId);
    }

    @Override
    @Transactional
    public PostResponseDto getPostWithCache(Long postId) {

        String cacheKey = redisProperties.cache().postCacheName() + postId;
        PostResponseDto cachedPost = postRedisTemplate.opsForValue().get(cacheKey);

        if (cachedPost != null) {
            log.info("Returning cached post for id: {}", postId);
            return cachedPost;
        }

        log.info("Cache miss for post id: {}. Fetching from external service...", postId);
        try {
            Optional<Post> optionalPost = postRepository.findById(postId);
            if (optionalPost.isPresent()) {
                PostResponseDto postResponseDto = postMapper.toPostResponseDto(optionalPost.get());
                postRedisTemplate.opsForValue().set(
                        cacheKey,
                        postResponseDto,
                        redisProperties.cache().postTtlMinutes(),
                        TimeUnit.MINUTES
                );
                return postResponseDto;
            } else {
                return null;
            }

        } catch (FeignException e) {
            log.error("Error fetching post from repository for id: {}", postId, e);
            throw new DataFetchException("Failed to fetch post for id: " + postId);
        }

    }

    public void incrementPostLikesCounter(long postId) {
        updatePostLikesCounter(postId, 1L);
    }

    public void decrementPostLikesCounter(long postId) {
        updatePostLikesCounter(postId, -1L);
    }

    @Override
    public void addCommentToHash(long postId, PostCommentEvent postCommentEvent) {

        final int maxComments = newsFeedProperties.commentsNumber();
        PostResponseDto postResponseDto = getPostWithCache(postId);
        LinkedHashSet<FeedItemCommentDto> comments = postResponseDto.comments();

        if (comments.size() >= maxComments) {
            Iterator<FeedItemCommentDto> iterator = comments.iterator();
            if (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }
        }
        comments.add(commentMapper.toFeedItemCommentDto(postCommentEvent));

        PostResponseDto updatedPostResponseDto = postMapper.toPostResponseDto(postResponseDto, comments);
        String cacheKey = redisProperties.cache().postCacheName() + postId;

        postRedisTemplate.opsForValue().set(
                cacheKey,
                updatedPostResponseDto,
                redisProperties.cache().postTtlMinutes(),
                TimeUnit.MINUTES
        );
        log.info("Comments for post {} updated. Added new comment {}", postId, postCommentEvent.commentId());
    }

    private void updatePostLikesCounter(long postId, long delta) {
        String cacheKey = redisProperties.cache().postCacheName() + postId;
        PostResponseDto postResponseDto = postRedisTemplate.opsForValue().get(cacheKey);

        if (postResponseDto != null) {
            log.info("Returning cached post for id: {}", postId);
            long likesCounter = postResponseDto.postLikesCounter();
            likesCounter += delta;
            if (likesCounter < 0) {
                likesCounter = 0;
            }
            //TODO хорошо бы переписать через маппер
            PostResponseDto incrementedPostResponseDto = PostResponseDto.builder()
                    .postLikesCounter(likesCounter)
                    .id(postResponseDto.id())
                    .content(postResponseDto.content())
                    .createdAt(postResponseDto.createdAt())
                    .authorId(postResponseDto.authorId())
                    .projectId(postResponseDto.projectId())
                    .isPublished(postResponseDto.isPublished())
                    .build();

            postRedisTemplate.opsForValue().set(
                    cacheKey,
                    incrementedPostResponseDto,
                    redisProperties.cache().postTtlMinutes(),
                    TimeUnit.MINUTES);
            log.info("For post {} updated likes counter to {}", postId, likesCounter);
        }
    }

    private Post getPostById(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        return optionalPost.orElseThrow(() -> new IllegalArgumentException("Post not found, Id = " + postId));
    }

    @Override
    public List<PostResponseDto> findAllByFilter(PostFilterDto filter) {
        return postMapper.toPostResponseDtos(findAllPostsByFilter(filter));
    }

    @Override
    @Transactional
    public List<PostResponseDto> getPostsByUser(long userId) {
        // TODO предполагается, что тут должно быть ограничение кол-ва постов, и только опубликованные посты
        return postMapper.toPostResponseDtos(postRepository.findByAuthorId(userId));
    }

    private List<Post> preparePostList(List<Post> posts) {
        return posts.stream()
                .map(this::preparePostToPublish)
                .toList();
    }

    private Post preparePostToPublish(Post post) {
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        return post;
    }

    private List<Post> findAllPostsByFilter(PostFilterDto filter) {
        Specification<Post> specification = getPostSpecification(filter);
        return postRepository.findAll(specification);
    }


    private Post copyPostData(Post sourcePost, Post targetPost) {
        targetPost.setContent(sourcePost.getContent());
        return targetPost;
    }

    private Specification<Post> getPostSpecification(PostFilterDto filter) {
        return postSpecificationFilters.stream()
                .filter(spec -> spec.isApplicable(filter))
                .map(spec -> spec.apply(filter))
                .reduce(Specification::and)
                .orElse(null);
    }
}
