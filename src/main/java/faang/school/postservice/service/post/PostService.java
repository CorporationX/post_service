package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.redis.PostDtoRedis;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.entity.redis.Posts;
import faang.school.postservice.exception.PostValidationException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.messaging.redis.publisher.post.PostEventPublisher;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import faang.school.postservice.service.publisher.EventPublisherService;
import faang.school.postservice.service.redis.RedisPostService;
import faang.school.postservice.service.redis.RedisUserService;
import faang.school.postservice.service.user.UserService;
import faang.school.postservice.validator.post.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final RedisPostRepository redisPostRepository;
    private final RedisUserRepository redisUserRepository;
    private final PostMapper postMapper;
    private final PostValidator postValidator;
    private final UserService userService;
    private final RedisUserService redisUserService;
    private final RedisPostService redisPostService;
    private final PostEventPublisher postEventPublishers;
    private final EventPublisherService eventPublisherService;

    @Value("${spring.data.redis.cache.capacity.queue}")
    private int capacityQueue;

    public PostDto create(PostDto postDto) {
        postValidator.validateCreate(postDto);
//        boolean authorExists = postValidator.checkIfAuthorExists(postDto);
//        if (!authorExists) {
//            throw new PostValidationException("Author doesn't exists on system!");
//        }

        Post post = postMapper.toEntity(postDto);
        post.setPublished(false);
        post.setDeleted(false);

        Post savePost = postRepository.save(post);
        log.info("Post: {} saved", savePost);

        UserDto userDto = userService.getUser(savePost.getAuthorId());
        eventPublisherService.sendFollowersEventToKafka(savePost.getId(), userDto.getFollowersId());

        PostDto postDtoResponse = postMapper.toDto(savePost);

        // публикация в редис
        redisPostService.save(savePost.getId(), savePost, capacityQueue);
        redisUserService.save(userDto.getId(), userDto);

        return postDtoResponse;
    }

    public PostDto publish(Long postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        postValidator.validatePublish(postOptional);

        Post post = postOptional.get();
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        postEventPublishers.publish(postMapper.toPostEvent(post));

        return postMapper.toDto(postRepository.save(post));
    }

    public PostDto update(Long postId, PostDto postDto) {
        postValidator.validateUpdate(postId, postDto);

        Post post = postMapper.toEntity(postDto);
        post.setUpdatedAt(LocalDateTime.now());

        return postMapper.toDto(postRepository.save(post));
    }

    public PostDto softDelete(Long postId) {
        Optional<Post> postOptional = postRepository.findById(postId);

        Post post = postOptional.orElseThrow(
                () -> new PostValidationException("Post with id " + postId + " doesn't exists"));

        post.setPublished(false);
        post.setDeleted(true);

        return postMapper.toDto(postRepository.save(post));
    }

    public PostDto getById(Long postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        Post post = postOptional.orElseThrow(
                () -> new PostValidationException("Post with id " + postId + " doesn't exists"));
//        postEventPublishers.publish(postMapper.toPostEvent(post));
        eventPublisherService.sendPostEventToRedis(post);
        eventPublisherService.sendPostViewEventToKafka(post);

        return postMapper.toDto(post);
    }

    public List<PostDto> getAllUnpublishedPostsForAuthor(Long authorId) {
        List<Post> posts = postRepository.findByAuthorId(authorId);
        if (posts != null) {
            return getSortedPostsByFilter(posts,
                    post -> !post.isPublished() && !post.isDeleted(),
                    Comparator.comparing(Post::getCreatedAt).reversed());
        }

        throw new PostValidationException("No posts for author with id " + authorId);
    }

    public List<PostDto> getAllUnpublishedPostsForProject(Long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);
        if (posts != null) {
            return getSortedPostsByFilter(posts,
                    post -> !post.isPublished() && !post.isDeleted(),
                    Comparator.comparing(Post::getCreatedAt).reversed());
        }

        throw new PostValidationException("No posts for author with id " + projectId);
    }

    public List<PostDto> getAllPublishedPostsForAuthor(Long authorId) {
        List<Post> posts = postRepository.findByAuthorId(authorId);

        if (posts != null) {
            return getSortedPostsByFilter(posts,
                    post -> post.isPublished() && !post.isDeleted(),
                    Comparator.comparing(Post::getPublishedAt).reversed());
        }

        throw new PostValidationException("No posts for author with id " + authorId);
    }

    public List<PostDto> getAllPublishedPostsForProject(Long projectId) {
        List<Post> posts = postRepository.findByProjectId(projectId);

        if (posts != null) {
            return getSortedPostsByFilter(posts,
                    post -> post.isPublished() && !post.isDeleted(),
                    Comparator.comparing(Post::getPublishedAt).reversed());
        }

        throw new PostValidationException("No posts for author with id " + projectId);
    }

    private List<PostDto> getSortedPostsByFilter(List<Post> posts,
                                                 Predicate<Post> predicate, Comparator<Post> comparator) {
        return posts.stream()
                .filter(predicate)
                .sorted(comparator)
                .map(postMapper::toDto)
                .toList();
    }
}