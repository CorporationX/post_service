package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.dto.event.PostPublishedEvent;
import faang.school.postservice.dto.filter.PostFilterDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.filter.post.PostFilter;
import faang.school.postservice.kafka.Producer;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.model.redis.UserRedis;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.PostRedisRepository;
import faang.school.postservice.repository.redis.UserRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final PostRedisRepository postRedisRepository;
    private final UserRedisRepository userRedisRepository;
    private final UserServiceClient userServiceClient;
    private final PostValidator validator;
    private final PostMapper mapper;
    private final List<PostFilter> postFilters;
    private final Producer kafkaProducer;
    @Value("${spring.kafka.topic.post.published}")
    private String postPublishedTopic;

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
        saveToCache(publishedPost, userDto);
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
        updateInCacheIfExists(updatedEntity);
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
        deleteFromCacheIfExists(postId);
        log.info("Deleted post by id {}", deletedEntity.getId());

        return mapper.toDto(deletedEntity);
    }

    public PostDto getPost(Long postId) {
        return mapper.toDto(getEntityFromDB(postId));
    }

    public List<PostDto> getFilteredPosts(PostFilterDto filters) {
        List<PostFilter> actualPostFilters = postFilters.stream()
                .filter(f -> f.isApplicable(filters)).toList();

        return StreamSupport.stream(postRepository.findAll().spliterator(), false)
                .filter(post -> actualPostFilters.stream()
                        .allMatch(filter -> filter.test(post, filters)))
                .map(mapper::toDto)
                .toList();
    }

    private Post getEntityFromDB(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Post by id %s not found", postId)));
    }

    private void saveToCache(Post post, UserDto userDto) {
        postRedisRepository.save(mapper.toRedis(post));
        if (!userRedisRepository.existsById(userDto.getId())) {
            userRedisRepository.save(new UserRedis(userDto.getId(), userDto.getUsername()));
        }
    }

    private void sendPostPublishedEvent(Post post, UserDto userDto) {
        PostPublishedEvent event = new PostPublishedEvent(post.getId(), userDto.getFollowersIds());
        kafkaProducer.send(postPublishedTopic, event);
    }

    private void updateInCacheIfExists(Post updatedEntity) {
        if (updatedEntity.isPublished()) {
            Optional<PostRedis> old = postRedisRepository.findById(updatedEntity.getId());
            if (old.isPresent()) {
                PostRedis postRedis = old.get();
                postRedis.setContent(updatedEntity.getContent());
                postRedisRepository.save(mapper.toRedis(updatedEntity));
            }
        }
    }

    private void deleteFromCacheIfExists(Long postId) {
        if (postRedisRepository.existsById(postId)) {
            postRedisRepository.deleteById(postId);
        }
    }
}
