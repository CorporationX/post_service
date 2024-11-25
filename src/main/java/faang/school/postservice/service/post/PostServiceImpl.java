package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event.kafka.PostEvent;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.PostException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.AuthorRedis;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.producer.KafkaPostProducer;
import faang.school.postservice.repository.RedisAuthorRepository;
import faang.school.postservice.repository.post.PostRepository;
import faang.school.postservice.repository.post.RedisPostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;
    private final KafkaPostProducer kafkaPostProducer;
    private final RedisPostRepository redisPostRepository;
    private final RedisAuthorRepository redisAuthorRepository;

    @Override
    public PostDto createPost(PostDto postDto) {
        isPostAuthorExist(postDto);

        postDto.setCreatedAt(LocalDateTime.now());

        Post post = postMapper.toEntity(postDto);
        post.setPublished(false);
        post.setDeleted(false);

        postRepository.save(post);
        savedToRedisAndSendEventToKafka(postMapper.toPostRedis(post));
        return postDto;
    }

    @Override
    public PostDto publishPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        if (post.isPublished()) {
            throw new PostException("Post is already published");
        }

        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        postRepository.save(post);
        savedToRedisAndSendEventToKafka(postMapper.toPostRedis(post));
        return postMapper.toDto(post);
    }

    @Override
    public PostDto updatePost(PostDto postDto) {
        Post post = postRepository.findById(postDto.getId())
                .orElseThrow(EntityNotFoundException::new);

        post.setUpdatedAt(LocalDateTime.now());
        post.setContent(postDto.getContent());

        postRepository.save(post);

        return postMapper.toDto(post);
    }

    @Override
    public PostDto deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        if (post.isDeleted()) {
            throw new PostException("post already deleted");
        }

        post.setPublished(false);
        post.setDeleted(true);
        postRepository.save(post);

        PostDto postDto = postMapper.toDto(post);
        postDto.setDeletedAt(LocalDateTime.now());

        return postDto;
    }

    @Override
    public PostDto getPost(Long id) {
        return postRepository.findById(id)
                .map(postMapper::toDto)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public List<PostDto> getAllNonPublishedByAuthorId(Long id) {
        validateUserExist(id);

        return filterNonPublishedPostsByTimeToDto(postRepository.findByAuthorId(id));
    }

    @Override
    public List<PostDto> getAllNonPublishedByProjectId(Long id) {
        validateProjectExist(id);

        return filterNonPublishedPostsByTimeToDto(postRepository.findByProjectId(id));
    }

    @Override
    public List<PostDto> getAllPublishedByAuthorId(Long id) {
        validateUserExist(id);

        return filterPublishedPostsByTimeToDto(postRepository.findByAuthorId(id));
    }

    @Override
    public List<PostDto> getAllPublishedByProjectId(Long id) {
        validateProjectExist(id);

        return filterPublishedPostsByTimeToDto(postRepository.findByProjectId(id));
    }

    private void validateUserExist(Long id) {
        userServiceClient.getUser(id);
    }

    private void validateProjectExist(Long id) {
        projectServiceClient.getProject(id);
    }

    private List<PostDto> filterPublishedPostsByTimeToDto(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    private List<PostDto> filterNonPublishedPostsByTimeToDto(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .map(postMapper::toDto)
                .toList();
    }

    private void isPostAuthorExist(PostDto postDto) {
        if (postDto.getAuthorId() != null) {
            userServiceClient.getUser(postDto.getAuthorId());
        } else {
            projectServiceClient.getProject(postDto.getProjectId());
        }
    }

    private void savedToRedisAndSendEventToKafka(PostRedis postRedis) {
        long authorId = postRedis.getAuthorId();
        UserDto author = userServiceClient.getUser(authorId);
        redisPostRepository.save(postRedis);
        log.info("Saved post with ID: {} to Redis", postRedis.getId());
        redisAuthorRepository.save(new AuthorRedis(authorId, author.getUsername()));
        log.info("Saved author with ID: {} to Redis", author.getId());
        PostEvent postEvent = new PostEvent(postRedis.getId(),
                postRedis.getAuthorId(),
                author.getSubscribersId());

        kafkaPostProducer.sendPostEvent(postEvent);
    }
}
