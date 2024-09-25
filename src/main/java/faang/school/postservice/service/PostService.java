package faang.school.postservice.service;


import faang.school.postservice.client.HashtagServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.event.PostEvent;
import faang.school.postservice.dto.event.kafka.NewPostEvent;
import faang.school.postservice.dto.event.kafka.PostViewEvent;
import faang.school.postservice.dto.hashtag.HashtagRequest;
import faang.school.postservice.mapper.CachePostMapper;
import faang.school.postservice.model.post.CachePost;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostContextMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.producer.KafkaPostProducer;
import faang.school.postservice.producer.KafkaPostViewProducer;
import faang.school.postservice.redisPublisher.PostEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.elasticsearchService.ElasticsearchService;
import faang.school.postservice.validator.PostServiceValidator;
import feign.FeignException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final SpellCheckerService spellCheckerService;
    private final PostMapper postMapper;
    private final PostServiceValidator postServiceValidator;
    private final HashtagServiceClient hashtagServiceClient;
    private final ElasticsearchService elasticsearchService;
    private final EntityManager entityManager;
    private final PostContextMapper context;
    private final PostEventPublisher postEventPublisher;
    private final UserContext userContext;
    private final KafkaPostProducer kafkaPostProducer;
    private final UserServiceClient userServiceClient;
    private final KafkaPostViewProducer kafkaPostViewProducer;
    private final CachePostMapper cachePostMapper;

    @Value("${spring.data.hashtag-cache.size.post-cache-size}")
    private int postCacheSize;
    @Value("${spring.feed.max-size}")
    private int maxSizeFeed;

    @Async(value = "threadPool")
    @Transactional
    public void correctPostsContent(List<Post> postList) {
        for (Post post : postList) {
            Optional<String> checkedPostContent = spellCheckerService.checkMessage(post.getContent());
            checkedPostContent.ifPresent(post::setContent);
        }
        postRepository.saveAll(postList);
    }

    @Transactional
    public PostDto createPost(PostDto postDto) {
        postServiceValidator.validateCreatePost(postDto);
        saveHashtags(postDto.getHashtagNames());
        List<Hashtag> hashtags = getHashtagsByNames(postDto.getHashtagNames());

        Post post = Post.builder()
                .authorId(postDto.getAuthorId())
                .projectId(postDto.getProjectId())
                .content(postDto.getContent())
                .hashtags(hashtags.stream()
                        .map(entityManager::merge)
                        .toList())
                .build();

        post = postRepository.save(post);
        sendToRedisPublisher(userContext.getUserId(), post.getId());
        PostDto postDtoForReturns = postMapper.toDto(post);
        elasticsearchService.indexPost(postDtoForReturns);
        return postDtoForReturns;
    }

    @Transactional
    public PostDto updatePost(PostDto postDto) {
        Post post = getPostById(postDto.getId());
        postServiceValidator.validateUpdatePost(post, postDto);
        saveHashtags(postDto.getHashtagNames());

        post.setHashtags(new ArrayList<>(getHashtagsByNames(postDto.getHashtagNames())));
        post.setContent(postDto.getContent());

        post = postRepository.save(post);
        PostDto postDtoForReturns = postMapper.toDto(post);
        elasticsearchService.indexPost(postDtoForReturns);
        return postDtoForReturns;
    }

    @Transactional
    public PostDto publishPost(PostDto postDto) {
        Post post = getPostById(postDto.getId());
        postServiceValidator.validatePublishPost(post);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        post = postRepository.save(post);
        kafkaPostProducer.send(NewPostEvent.builder()
                .id(post.getId())
                .subscribersIds(userServiceClient.getFollowerIds(post.getId()))
                .build());
        kafkaPostViewProducer.send(PostViewEvent.builder()
                .postId(post.getId())
                .userId(post.getAuthorId())
                .build());

        return postMapper.toDto(post);
    }

    @Transactional
    public PostDto deletePost(Long postId) {
        Post post = getPostById(postId);
        postServiceValidator.validateDeletePost(post);
        post.setDeleted(true);
        if (post.isPublished()) {
            post.setPublished(false);
        }

        post = postRepository.save(post);
        elasticsearchService.removePost(postId);
        return postMapper.toDto(post);
    }

    public PostDto getPostDtoById(Long postId) {
        return postMapper.toDto(getPostById(postId));
    }

    public List<PostDto> getAllDraftPostsByUserId(Long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        List<Post> filteredPosts = posts.stream()
                .filter(post -> !post.isPublished())
                .toList();

        return postMapper.toDto(sortPostsByCreateAt(filteredPosts));
    }

    public List<PostDto> getAllDraftPostsByProjectId(Long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        List<Post> filteredPosts = posts.stream()
                .filter(post -> !post.isPublished())
                .toList();

        return postMapper.toDto(sortPostsByCreateAt(filteredPosts));
    }

    public List<PostDto> getAllPublishPostsByUserId(Long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        List<Post> filteredPosts = posts.stream()
                .filter(Post::isPublished)
                .toList();

        return postMapper.toDto(sortPostsByPublishAt(filteredPosts));
    }

    public List<PostDto> getAllPublishPostsByProjectId(Long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        List<Post> filteredPosts = posts.stream()
                .filter(Post::isPublished)
                .toList();

        return postMapper.toDto(sortPostsByPublishAt(filteredPosts));
    }

    public List<PostDto> findPostsByHashtag(String hashtagName, int page, int size) {
        List<PostDto> postDtos = hashtagServiceClient.findPostsByHashtag(hashtagName).getPosts();
        if (!postDtos.isEmpty()) {
            if (postDtos.size() == postCacheSize) {
                long lastCachedPostId = postDtos.get(postDtos.size() - 1).getId();
                postDtos.addAll(elasticsearchService.searchPostsByHashtagAndByMostId(hashtagName, lastCachedPostId));
            }
            if (postDtos.size() < (page * size)) {
                return Collections.emptyList();
            }

            return postDtos.subList(page * size, Math.min(postDtos.size(), (page + 1) * size));
        } else {
            return elasticsearchService.searchPostsByHashtag(hashtagName, page, size);
        }
    }

    public Post getPost(long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post with the same id does not exist"));
        long countLike;
        if (post.getLikes() == null) {
            countLike = 0;
        }
        countLike = post.getLikes().size();
        context.getCountLikeEveryonePost().put(postId, countLike);

        return post;
    }

    public List<PostDto> findPostsByHashtagForCache(String hashtagName, int page, int size) {
        return elasticsearchService.searchPostsByHashtag(hashtagName, page, size);
    }

    public Post findPostByIdWithLikes(long postId) {
        return postRepository.findByIdWithLikes(postId).orElseThrow(() -> {
            String errorMsg = String.format("Post id: %d not found", postId);
            log.error(errorMsg);
            return new EntityNotFoundException(errorMsg);
        });
    }

    public List<Post> findPostsByAuthorIds(List<Long> authorIds, PageRequest pageRequest) {
        return postRepository.findPostsByAuthorIds(authorIds, pageRequest);
    }

    public List<PostDto> getPostsDtoByIds(List<Long> postIds) {
        return postMapper.toDto(postRepository.findPostsByIds(postIds));
    }

    public List<Post> getPostsByIdsWithLikes(List<Long> postIds) {
        return postRepository.findPostsByIdsWithLikes(postIds);
    }

    public List<CachePost> getPostsByAuthorsIds(List<Long> authorsIds) {
        List<Post> posts = postRepository.findPostsByAuthorIds(authorsIds,
                PageRequest.of(0, maxSizeFeed));

        return cachePostMapper.convertPostsToCachePosts(posts);
    }

    private List<Post> sortPostsByCreateAt(List<Post> posts) {
        return posts.stream()
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .toList();
    }

    private List<Post> sortPostsByPublishAt(List<Post> posts) {
        return posts.stream()
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .toList();
    }

    private void sendToRedisPublisher(long userId, long postId) {
        PostEvent event = PostEvent.builder()
                .authorId(userId)
                .postId(postId)
                .build();
        postEventPublisher.publish(event);
    }

    private Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.error("Post ID " + postId + " not found");
                    return new EntityNotFoundException("Post ID " + postId + " not found");
                });
    }

    @Retryable(retryFor = FeignException.class, maxAttempts = 3, backoff = @Backoff(delay = 3000))
    public void saveHashtags(List<String> hashtagNames) {
        hashtagServiceClient.saveHashtags(HashtagRequest.builder()
                .hashtagNames(hashtagNames)
                .build());
        log.info("Hashtags have been saved successfully");
    }

    @Retryable(retryFor = FeignException.class, maxAttempts = 3, backoff = @Backoff(delay = 3000))
    public List<Hashtag> getHashtagsByNames(List<String> hashtagNames) {
        List<Hashtag> hashtags = hashtagServiceClient.getHashtagsByNames(HashtagRequest.builder()
                .hashtagNames(hashtagNames)
                .build()).getHashtags();
        log.info("Hashtags request was completed successfully");

        return hashtags;
    }
}
