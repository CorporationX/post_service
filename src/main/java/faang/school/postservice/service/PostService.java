package faang.school.postservice.service;


import faang.school.postservice.client.HashtagServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.event.PostEvent;
import faang.school.postservice.dto.event.kafka.PostCreatedEvent;
import faang.school.postservice.dto.event.kafka.PostViewEvent;
import faang.school.postservice.dto.hashtag.HashtagRequest;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.PostContextMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.chache.PostCache;
import faang.school.postservice.model.chache.UserCache;
import faang.school.postservice.producer.KafkaPostProducer;
import faang.school.postservice.producer.KafkaProducer;
import faang.school.postservice.redisPublisher.PostEventPublisher;
import faang.school.postservice.repository.cache.PostCacheRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.cache.UserCacheRepository;
import faang.school.postservice.service.elasticsearchService.ElasticsearchService;
import faang.school.postservice.validator.PostServiceValidator;
import feign.FeignException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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
    private final KafkaProducer kafkaProducer;
    private final UserServiceClient userServiceClient;
    private final PostCacheRepository postCacheRepository;
    private final UserCacheRepository userCacheRepository;



    @Value("${spring.data.hashtag-cache.size.post-cache-size}")
    private int postCacheSize;

    @Value("${spring.post.cache.ttl}")
    private long postTtl;

    @Value("${spring.user.cache.ttl}")
    private long userTtl;

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

    @Async(value = "threadPool")
    @Transactional
    public PostDto publishPost(PostDto postDto) {
        Post post = getPostById(postDto.getId());
        postServiceValidator.validatePublishPost(post);
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        post = postRepository.save(post);

        postCacheRepository.save(PostCache.builder()
                .postId(post.getId())
                .authorId(post.getAuthorId())
                .projectId(post.getProjectId())
                .publishedAt(post.getPublishedAt())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .content(post.getContent())
                .ttl(postTtl)
                .build());

        List<Long> followersIds = userServiceClient.getFollowerIds(userContext.getUserId());
        UserDto author = userServiceClient.getUser(post.getAuthorId());
        userCacheRepository.save(UserCache.builder()
                .id(author.getId())
                .name(author.getUsername())
                .ttl(userTtl)
                .build());

        PostCreatedEvent newPostEvent = PostCreatedEvent.builder()
                .postId(post.getId())
                .authorId(post.getAuthorId())
                .followersId(followersIds)
                .build();
        kafkaProducer.sendEvent(newPostEvent);
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
        Post post = getPostById(postId);
        kafkaProducer.sendEvent(PostViewEvent.builder()
                .userId(userContext.getUserId())
                .postId(post.getId()));
        return postMapper.toDto(post);
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

    public List<PostDto> getPostsByIds(List<Long> postIds) {
        return postMapper.toDto(postRepository.findPostsByIds(postIds));
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
