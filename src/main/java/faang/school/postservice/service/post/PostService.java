package faang.school.postservice.service.post;

import faang.school.postservice.FixedSizeLinkedHashMap;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostServiceValidator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PostService {
    @Value("${spring.kafka.topic-name.posts:posts}")
    private String postTopic;
    @Value("${spring.kafka.topic-name.post-views-topic}")
    private String postViewsTopic;

    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final PostServiceValidator<PostDto> validator;
    private final KafkaTemplate <Long, Object> postKafkaTemplate;
    private final KafkaTemplate <Long, Long> postViewKafkaTemplate;
    private final UserServiceClient userServiceClient;
    private final RedisTemplate<Long, FixedSizeLinkedHashMap<Long, PostDto>> redisTemplate;

    public PostService(PostMapper postMapper, PostRepository postRepository, PostServiceValidator<PostDto> validator,
                       @Qualifier("postViewKafkaTemplate")KafkaTemplate<Long, Object> postKafkaTemplate, UserServiceClient userServiceClient,
                       @Qualifier("postViewKafkaTemplate")KafkaTemplate <Long, Long> postViewKafkaTemplate,
                       @Qualifier("redisCacheTemplate")RedisTemplate<Long, FixedSizeLinkedHashMap<Long, PostDto>> redisTemplate) {
        this.postMapper = postMapper;
        this.postRepository = postRepository;
        this.validator = validator;
        this.postKafkaTemplate = postKafkaTemplate;
        this.postViewKafkaTemplate = postViewKafkaTemplate;
        this.userServiceClient = userServiceClient;
        this.redisTemplate = redisTemplate;
    }

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

        sendPostEventToKafka(savedPost);
//        var authorId = post.getAuthorId();
//        var author = userServiceClient.getUser(authorId);
//        author.getFollowers().forEach(followerId -> {
//            var posts = redisTemplate.opsForValue().get(followerId);
//            posts.put(postId, postDto);
//        });

        return postMapper.toDto(savedPost);
    }

    private void sendPostEventToKafka(Post post){
        Map<String, Object> event = new HashMap<>();
        var postAuthorFollowersIds = getPostAuthorFollowers(post);
        var postId = post.getId();
        event.put("postId", postId);
        event.put("followersIds", postAuthorFollowersIds);
        postKafkaTemplate.send(postTopic, post.getId(), event);
    }

    private List<Long> getPostAuthorFollowers(Post post) {
        var postAuthorId = post.getAuthorId();
        var author = userServiceClient.getUser(postAuthorId);
        return author.getFollowers();
    }

    private void validatePostPublishing(Post post) {
        if (post.isPublished()) {
            throw new IllegalArgumentException("Post is already published");
        }
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

        sendPostViewEventToKafka(post);
        return postMapper.toDto(post);
    }

    private void sendPostViewEventToKafka(Post post){
        Map<String, Long> event = new HashMap<>();
        var postId = post.getId();
        event.put("postId", postId);
        event.put("authorId", post.getAuthorId());
        postKafkaTemplate.send(postViewsTopic, postId, event);
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

    private Post getPostByIdOrFail(long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));
    }
}
