package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.filter.PostFilterDto;
import faang.school.postservice.dto.post.KafkaPostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.filter.post.PostFilter;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.KafkaPostProducer;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final PostValidator validator;
    private final PostMapper mapper;
    private final PostDataPreparer preparer;
    private final List<PostFilter> postFilters;
    private final KafkaPostProducer kafkaPostProducer;
    private final UserServiceClient userClient;
    private final RedisPostRepository redisPostRepository;

    public PostDto create(PostDto postDto) {
        validator.validateBeforeCreate(postDto);

        Post postEntity = mapper.toEntity(postDto);
        postEntity = preparer.prepareForCreate(postDto, postEntity);

        Post createdEntity = postRepository.save(postEntity);
        KafkaPostDto kafkaDto = mapper.toKafkaDto(createdEntity);
        kafkaDto.setSubscribers(userClient.getUser(createdEntity.getAuthorId()).getMenteesIds());
        redisPostRepository.save(mapper.toRedisPost(createdEntity));
        kafkaPostProducer.publish(kafkaDto);
        log.info("Created a post: {}", createdEntity);

        return mapper.toDto(createdEntity);
    }

    public PostDto publish(Long postId) {
        Post entity = getPostEntity(postId);
        validator.validatePublished(entity);
        Post publishedPost = preparer.prepareForPublish(entity);

        publishedPost = postRepository.save(publishedPost);
        log.info("Published post: {}", publishedPost);

        return mapper.toDto(publishedPost);
    }

    public PostDto update(PostDto postDto) {
        Post entity = getPostEntity(postDto.getId());
        validator.validateBeforeUpdate(postDto, entity);

        Post updatedEntity = preparer.prepareForUpdate(postDto, entity);
        updatedEntity = postRepository.save(updatedEntity);
        log.info("Updated post: {}", updatedEntity);

        return mapper.toDto(updatedEntity);
    }

    public PostDto delete(Long postId) {
        Post entity = getPostEntity(postId);
        validator.validateDeleted(entity);

        entity.setPublished(false);
        entity.setDeleted(true);
        entity.setUpdatedAt(LocalDateTime.now());
        Post deletedEntity = postRepository.save(entity);
        log.info("Deleted post: {}", deletedEntity);

        return mapper.toDto(deletedEntity);
    }

    public PostDto getPost(Long postId) {
        return mapper.toDto(getPostEntity(postId));
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

    private Post getPostEntity(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new EntityNotFoundException("Такого сообщения не существует."));
    }
}
