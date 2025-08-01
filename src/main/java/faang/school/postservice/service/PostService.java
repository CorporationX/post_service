package faang.school.postservice.service;

import faang.school.postservice.cache.AuthorCacheService;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.kafka.KafkaProducerService;
import faang.school.postservice.dto.post.PostEventDto;
import faang.school.postservice.dto.kafka.PostViewEvent;
import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.kafka.KafkaPostViewProducer;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    @Value("${kafka.topics.posts.create-post}")
    private String topic;

    @Value("${limit_not_verified_posts}")
    private int limitNotVerifiedPosts;

    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final KafkaProducerService kafka;
    private final UserServiceClient feignClient;
    private final KafkaPostViewProducer kafkaPostViewProducer;
    private final AuthorCacheService authorCacheService;
    private final RedisService redisService;

    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("There is no such id = " + id));
    }

    public PostResponseDto getPostById(long id, long userId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("There is no such id = " + id));

        PostViewEvent event = PostViewEvent.builder()
                .postId(id)
                .userId(userId)
                .viewedAt(LocalDateTime.now())
                .build();
        kafkaPostViewProducer.send(event);

        return postMapper.toDto(post);
    }

    public PostResponseDto createDraftPost(PostRequestDto request) {
        Post post = postMapper.toEntity(request);

        PostResponseDto responseDto = postMapper.toDto(postRepository.save(post));

        PostEventDto event = new PostEventDto(
                responseDto.id(),
                responseDto.authorId(),
                feignClient.getUserFolowees(responseDto.authorId())
        );
        kafka.sendMessage(event, topic);

        return responseDto;
    }

    public PostResponseDto publishPost(Long postId) {
        Post post = getPostById(postId);
        if (post.isPublished()) {
            throw new IllegalArgumentException("Post already posted.");
        }
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());

        Post savedPost = postRepository.save(post);

        authorCacheService.cacheAuthor(savedPost.getAuthorId());

        return postMapper.toDto(savedPost);
    }

    public PostResponseDto updatePost(Long postId, PostRequestDto request) {
        Post post = getPostById(postId);
        post.setContent(request.content());
        return postMapper.toDto(postRepository.save(post));
    }

    public PostResponseDto deletePost(Long postId) {
        Post post = getPostById(postId);
        post.setDeleted(true);
        return postMapper.toDto(postRepository.save(post));
    }

    public List<PostResponseDto> getAllNotDeletedDraftsByAuthorId(Long authorId) {
        return getDraftsById(postRepository.findByAuthorId(authorId));
    }

    public List<PostResponseDto> getAllNotDeletedDraftsByProjectId(Long projectId) {
        return getDraftsById(postRepository.findByProjectId(projectId));
    }

    public List<PostResponseDto> getAllPostsByAuthorId(Long authorId) {
        return getPostsById(postRepository.findByAuthorId(authorId));
    }

    public List<PostResponseDto> getAllPostsByProjectId(Long projectId) {
        return getPostsById(postRepository.findByProjectId(projectId));
    }

    public void banUsers() {
        List<Long> users = postRepository.findAllUsersWhereNotVerifiedMoreN(limitNotVerifiedPosts);

        if (!users.isEmpty()) {
            users.forEach(redisService::sendMessageToBanUsers);
            log.info("__________________users id send________________________________________________ ");
        }
    }

    private List<PostResponseDto> getDraftsById(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isDeleted() && !post.isPublished())
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostResponseDto::createdAt))
                .toList();
    }

    private List<PostResponseDto> getPostsById(List<Post> posts) {
        return posts.stream()
                .filter(post -> !post.isDeleted() && post.isPublished())
                .map(postMapper::toDto)
                .sorted(Comparator.comparing(PostResponseDto::publishedAt))
                .toList();
    }
}
