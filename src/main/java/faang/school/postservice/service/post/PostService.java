package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.kafka.producer.KafkaEventProducer;
import faang.school.postservice.kafka.events.PostFollowersEvent;
import faang.school.postservice.kafka.events.PostEvent;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.redis.mapper.AuthorCacheMapper;
import faang.school.postservice.redis.mapper.PostCacheMapper;
import faang.school.postservice.redis.repository.AuthorCacheRedisRepository;
import faang.school.postservice.redis.repository.PostCacheRedisRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    @Value("${spring.kafka.topic-name.posts:posts}")
    private String postTopic;
    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final PostServiceValidator<PostDto> validator;
    private final KafkaEventProducer kafkaEventProducer;
    private final UserServiceClient userServiceClient;
    private final PostCacheRedisRepository postCacheRedisRepository;
    private final PostCacheMapper postCacheMapper;
    private final AuthorCacheRedisRepository authorCacheRedisRepository;
    private final AuthorCacheMapper authorCacheMapper;

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

        postCacheRedisRepository.save(postCacheMapper.toPostCache(postDto));
        sendPostFollowersEventToKafka(savedPost);

        return postDto;
    }
    //TODO PRIVATE METHODS!
    private void sendPostFollowersEventToKafka(Post post){
        var postAuthorFollowersIds = getPostAuthorFollowers(post);
        var postId = post.getId();
        var event = PostFollowersEvent.builder()
                .authorId(post.getAuthorId())
                .postId(postId)
                .followersIds(postAuthorFollowersIds)
                .build();

        kafkaEventProducer.sendPostFollowersEvent(event);
    }

    private List<Long> getPostAuthorFollowers(Post post) {
        var postAuthorId = post.getAuthorId();
        var author = userServiceClient.getUser(postAuthorId);

        authorCacheRedisRepository.save(authorCacheMapper.toAuthorCache(author));

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
        var postId = post.getId();
        var event = PostEvent.builder()
                .id(postId)
                .authorId(post.getAuthorId())
                .build();
        kafkaEventProducer.sendPostViewEvent(postId, event);
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

    public List<PostDto> getPostsByIds(List<Long> postIds) {
        return postRepository.findAllById(postIds).stream()
                .map(postMapper::toDto)
                .toList();
    }
}