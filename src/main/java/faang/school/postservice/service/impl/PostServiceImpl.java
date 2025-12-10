package faang.school.postservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.PostViewedEvent;
import faang.school.postservice.dto.post.PostDraftDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostRedisDto;
import faang.school.postservice.exception.NotExistsException;
import faang.school.postservice.exception.NotSupportedDataException;
import faang.school.postservice.integration.project.service.ProjectServiceClient;
import faang.school.postservice.integration.user.service.UserServiceClient;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.PostRedis;
import faang.school.postservice.model.PostRedisByAuthor;
import faang.school.postservice.producer.AnalyticsEventProducer;
import faang.school.postservice.producer.AnalyticsEventProducer.AnalyticsEvent;
import faang.school.postservice.producer.PostEventProducer;
import faang.school.postservice.producer.PostViewedProducer;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.RedisPostRepository;
import faang.school.postservice.repository.RedisPostRepositoryByAuthor;
import faang.school.postservice.service.PostService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static faang.school.postservice.producer.AnalyticsEventProducer.EventType.POST_PUBLISHED;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final ProjectServiceClient projectServiceClient;
    private final UserServiceClient userServiceClient;
    private final AnalyticsEventProducer analyticsEventProducer;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final RedisPostRepository redisPostRepository;
    @Value("${redis.post-expire}")
    private String postExpire;
    private final PostEventProducer postEventProducer;
    private final PostViewedProducer postViewedProducer;
    private final RedisPostRepositoryByAuthor redisPostRepositoryByAuthor;


    @Override
    public PostDto findById(long id) {
        return postRepository.findById(id)
                .map(p -> postMapper.postToPostDto(p))
                .orElseThrow(() -> new EntityNotFoundException("Пост с id " + id + " не найден"));
    }

    @Override
    public void save(PostDraftDto postDraftDto) {
        postRepository.save(postMapper.postDraftDtoToPost(postDraftDto));
    }

    @Override
    public PostDto markPostAsDeleted(long id) {
        Post post = findPostEntityById(id);
        post.setDeleted(true);
        post.setPublished(false);
        return postMapper.postToPostDto(postRepository.save(post));
    }

    @Override
    public PostDto createPostDraft(PostDraftDto postDraftDto) {
        checkAuthorExists(postDraftDto);
        Post post = postMapper.postDraftDtoToPost(postDraftDto);
        post.setPublished(false);
        post.setDeleted(false);
        return postMapper.postToPostDto(postRepository.save(post));
    }

    @Override
    public void publishPost(Long postId, PostDraftDto postDraftDto) {
        Post post;
        if (postId == null) {
            if (postDraftDto == null) {
                throw new NotSupportedDataException("Если postId = null, то postDraftDto != null");
            }
//            checkAuthorExists(postDraftDto);
            post = postMapper.postDraftDtoToPost(postDraftDto);
            post.setPublished(true);
            post.setPublishedAt(LocalDateTime.now());
            post.setDeleted(false);
        } else {
            post = findPostEntityById(postId);
            if (post.isPublished()) {
                throw new NotSupportedDataException("Нельзя опубликовать пост повторно");
            }
            post.setPublished(true);
            post.setPublishedAt(LocalDateTime.now());
        }
        Post savedPost = postRepository.save(post);
        try {
//            String key = "authors:" + post.getAuthorId() + ":list";
//            redisTemplate.opsForList().rightPush(key, post);
//            redisTemplate.expire(key, Duration.ofMillis(Long.parseLong(postExpire)));

            PostRedisByAuthor postRedisByAuthor = postToPostRedisByAuthor(post);
            redisPostRepositoryByAuthor.save(postRedisByAuthor);
        } catch(Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Ошибка сохранения в кеш");
        }

        try {
//            String key = "id:" + post.getId() + ":list";
//            redisTemplate.opsForList().rightPush(key, post);
//            redisTemplate.expire(key, Duration.ofMillis(Long.parseLong(postExpire)));
            PostRedis postRedis = postToPostRedis(post);
            redisPostRepository.save(postRedis);
        } catch(Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Ошибка сохранения в кеш");
        }

        postEventProducer.sendPostEvent(postMapper.postToPostEvent(post));

        analyticsEventProducer.sendAnalyticsEvent(new AnalyticsEvent()
                .setEventType(POST_PUBLISHED)
                .setReceivedAt(LocalDateTime.now())
                .setActorId(savedPost.getAuthorId() != null ? savedPost.getAuthorId() : savedPost.getProjectId())
                .setReceiverId(0)
        );
    }

    @Override
    public PostDto updatePost(PostDto postDto, Long postId) {
        Post post = findPostEntityById(postId);
        post.setContent(postDto.getContent());
        postRepository.save(post);
        return postMapper.postToPostDto(post);
    }

    @Override
    public List<PostDto> getAllPostsByAuthorId(long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        return posts.stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(post -> postMapper.postToPostDto(post))
                .toList();
    }

    @Override
    public List<PostDto> getAllPostsByProjectId(long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        return posts.stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(post -> postMapper.postToPostDto(post))
                .toList();
    }

    @Override
    public List<PostDto> getAllPublishedPostsByAuthorId(long userId) {
        List<Post> posts = postRepository.findByAuthorId(userId);
        return posts.stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(post -> postMapper.postToPostDto(post))
                .toList();
    }

    @Override
    public List<PostDto> getAllPublishedPostsByProjectId(long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        return posts.stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted(Comparator.comparing(Post::getPublishedAt).reversed())
                .map(post -> postMapper.postToPostDto(post))
                .toList();
    }

    private void checkAuthor(PostDraftDto postDraftDto) {
        if (postDraftDto.getProjectId() == null) {
            if (postDraftDto.getAuthorId() == null) {
                throw new NotSupportedDataException("Необходимо укзать одно из этих полей: AuthorId или ProjectId");
            }
        }
    }

    private void checkAuthorExists(PostDraftDto postDraftDto) {
        checkAuthor(postDraftDto);
        if (postDraftDto.getAuthorId() != null) {
            if (userServiceClient.getUser(postDraftDto.getAuthorId()) == null) {
                throw new NotExistsException("Автор с id " + postDraftDto.getAuthorId() + " не найден в базе");
            }
        } else {
            if (projectServiceClient.getProject(postDraftDto.getProjectId()) == null) {
                throw new NotExistsException("Проект с id " + postDraftDto.getProjectId() + " не найден в базе");
            }
        }
    }

    private Post findPostEntityById(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Пост с id " + postId + " не найден"));
    }

    public void makePostViewed(long postId) {
        postRepository.findById(postId).ifPresent(p -> {
            p.setIsViewed(true);
            postRepository.save(p);
            postViewedProducer.sendPostViewedEvent(new PostViewedEvent(postId));
        });
    }

    private PostRedis postToPostRedis(Post post) {
        PostRedis postRedis = new PostRedis();
        postRedis.setPostId(post.getId());
        PostRedisDto postRedisDto = new PostRedisDto();
        postRedisDto.setId(post.getId());
        postRedisDto.setContent(post.getContent());
        postRedisDto.setAuthorId(post.getAuthorId());
        postRedisDto.setProjectId(post.getProjectId());
        postRedisDto.setPublished(post.isPublished());
        postRedisDto.setPublishedAt(post.getPublishedAt());
        postRedisDto.setDeleted(post.isDeleted());
        List<PostRedisDto> postRedisDtos = new ArrayList<>();
        postRedisDtos.add(postRedisDto);
        postRedis.setPost(postRedisDtos);
        return postRedis;
    }

    private PostRedisByAuthor postToPostRedisByAuthor(Post post) {
        PostRedisByAuthor postRedisByAuthor = new PostRedisByAuthor();
        postRedisByAuthor.setAuthorId(post.getAuthorId());
        PostRedisDto postRedisDto = new PostRedisDto();
        postRedisDto.setId(post.getId());
        postRedisDto.setContent(post.getContent());
        postRedisDto.setAuthorId(post.getAuthorId());
        postRedisDto.setProjectId(post.getProjectId());
        postRedisDto.setPublished(post.isPublished());
        postRedisDto.setPublishedAt(post.getPublishedAt());
        postRedisDto.setDeleted(post.isDeleted());
        List<PostRedisDto> postRedisDtos = new ArrayList<>();
        postRedisDtos.add(postRedisDto);
        postRedisByAuthor.setPost(postRedisDtos);
        return postRedisByAuthor;
    }

}
