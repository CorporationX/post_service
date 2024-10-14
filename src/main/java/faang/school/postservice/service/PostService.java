package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.kafka.event.post.PostPublishedEvent;
import faang.school.postservice.kafka.event.post.PostViewedEvent;
import faang.school.postservice.dto.filter.PostFilterDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.filter.post.PostFilter;
import faang.school.postservice.kafka.producer.Producer;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.cache.model.PostRedis;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.cache.service.PostRedisService;
import faang.school.postservice.cache.service.UserRedisService;
import faang.school.postservice.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final PostRedisService postRedisService;
    private final UserRedisService userRedisService;
    private final UserServiceClient userServiceClient;
    private final PostValidator validator;
    private final PostMapper mapper;
    private final List<PostFilter> postFilters;
    private final Producer kafkaProducer;
    @Value("${spring.kafka.topic.post.published}")
    private String postPublishedTopic;
    @Value("${spring.kafka.topic.post.viewed}")
    private String postViewedTopic;

    public PostDto create(PostDto dto) {
        validator.validateBeforeCreate(dto);

        Post entity = mapper.toEntity(dto);

        Post createdEntity = postRepository.save(entity);
        log.info("Created post by id {}", createdEntity.getId());

        return mapper.toDto(createdEntity);
    }

    @Transactional
    public PostDto publish(Long postId) {
        Post entity = getEntityFromDB(postId);
        validator.validateBeforePublishing(entity);

        entity.setPublished(true);
        entity.setPublishedAt(LocalDateTime.now());
        Post publishedPost = postRepository.save(entity);

        UserDto userDto = userServiceClient.getUser(publishedPost.getAuthorId());
        postRedisService.save(publishedPost);
        userRedisService.save(userDto);
        sendPostPublishedEvent(publishedPost, userDto);
        log.info("Published post by id {}", postId);

        return mapper.toDto(publishedPost);
    }

    @Transactional
    public PostDto update(PostDto dto) {
        Post entity = getEntityFromDB(dto.getId());
        validator.validateBeforeUpdate(dto, entity);

        entity.setContent(dto.getContent());

        Post updatedEntity = postRepository.save(entity);
        postRedisService.updateIfExists(updatedEntity);
        log.info("Updated post by id {}", updatedEntity.getId());

        return mapper.toDto(updatedEntity);
    }

    @Transactional
    public PostDto delete(Long postId) {
        Post entity = getEntityFromDB(postId);
        validator.validateBeforeDeleting(entity);

        entity.setPublished(false);
        entity.setDeleted(true);

        Post deletedEntity = postRepository.save(entity);
        postRedisService.deleteIfExists(postId);
        log.info("Deleted post by id {}", deletedEntity.getId());

        return mapper.toDto(deletedEntity);
    }

    @Transactional
    public PostDto getPost(Long id) {
        Post post = getEntityFromDB(id);
        long views = postRepository.incrementAndGetViewsById(id, 1);
        sendPostViewedEvent(id, views);
        return mapper.toDto(post);
    }

    public List<PostRedis> findAllByIdsWithLikes(List<Long> ids) {
        return mapper.toRedis(postRepository.findAllByIdsWithLikes(ids));
    }

    public List<PostRedis> findByAuthors(List<Long> authorIds, int postsCount) {
        return mapper.toRedis(postRepository.findByAuthors(authorIds, postsCount));
    }

    public List<PostRedis> findByAuthorsBeforeId(List<Long> authorIds, Long lastPostId, int postsCount) {
        return mapper.toRedis(postRepository.findByAuthorsBeforeId(authorIds, lastPostId, postsCount));
    }

    public List<PostDto> getFilteredPosts(PostFilterDto filters) {
        List<PostFilter> actualPostFilters = postFilters.stream()
                .filter(f -> f.isApplicable(filters)).toList();

        return postRepository.findAll().stream()
                .filter(post -> actualPostFilters.stream()
                        .allMatch(filter -> filter.test(post, filters)))
                .map(mapper::toDto)
                .toList();
    }

    public List<Long> findPostIdsByFollowerId(Long followerId, int batchSize) {
        return postRepository.findPostIdsByFollowerId(followerId, batchSize);
    }

    private Post getEntityFromDB(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Post by id %s not found", postId)));
    }

    private void sendPostPublishedEvent(Post post, UserDto userDto) {
        PostPublishedEvent event = new PostPublishedEvent(post.getId(), userDto.getFollowersIds());
        kafkaProducer.send(postPublishedTopic, event);
    }

    private void sendPostViewedEvent(Long id, long views) {
        PostViewedEvent event = new PostViewedEvent(id, views);
        kafkaProducer.send(postViewedTopic, event);
    }
}
