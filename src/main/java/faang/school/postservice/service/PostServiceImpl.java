package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CreatePostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.events.PostPublishedKafkaEvent;
import faang.school.postservice.exception.AuthorNotFoundException;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.UserMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaPostProducer;
import faang.school.postservice.publisher.RedisUserBanTopicPublisher;
import faang.school.postservice.redis_repository.post.PostRedisRepository;
import faang.school.postservice.redis_repository.user.UserRedisRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.utils.ListUtils;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final UserMapper userMapper;
    private final PostRedisRepository postRedisRepository;
    private final UserRedisRepository userRedisRepository;

    private final UserServiceClient userServiceClient;
    private final ProjectServiceClient projectServiceClient;

    private final RedisUserBanTopicPublisher redisUserBanTopicPublisher;

    private final KafkaPostProducer kafkaPostProducer;

    @Value("${app.moderation.post-count-threshold}")
    private int postCountThreshold;

    @Value("${news-feed.posts.redis-ttl:86400}")
    private long postRedisTtl;

    @Value("${news-feed.users.redis-ttl:86400}")
    private long userRedisTtl;

    @Value("${news-feed.posts.kafka-publish-batch-size:86400}")
    private int kafkaEventBatchSize;

    public CreatePostDto create(CreatePostDto createPostDto) {
        validateContent(createPostDto);
        var userDto = validateAuthor(createPostDto.authorId(), createPostDto.projectId());
        var post = postMapper.toEntity(createPostDto);

        postRepository.save(post);
        log.info("Post created: {}", post);

        if (userDto != null) {
            var postRedis = postMapper.toPostRedis(post);
            postRedis.setTimeToLive(postRedisTtl);
            postRedisRepository.save(postRedis);

            var userRedis = userMapper.toUserRedis(userDto);
            userRedis.setTimeToLive(userRedisTtl);
            userRedisRepository.save(userRedis);

            var followerIds = getUserFollowers(createPostDto.authorId()).stream().map(UserDto::id).toList();

            ListUtils.chunk(followerIds, kafkaEventBatchSize).parallel().forEach(followerIdsChunk -> {
                var eventToPublish = PostPublishedKafkaEvent.builder()
                        .postId(post.getId())
                        .authorFollowerIds(followerIdsChunk)
                        .build();
                kafkaPostProducer.sendEvent(eventToPublish);
            });
        }

        return postMapper.toCreatedPostDto(post);
    }

    @Override
    public Post findPostById(long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException(String.format("Post with id %s not found", postId)));
    }

    @Override
    public void banUsersIfRequired() {
        List<Long> authorIdsToBan;
        try {
            authorIdsToBan = postRepository.findAuthorIdsByUnverifiedPostsThreshold(postCountThreshold);
        } catch (DataAccessException ex) {
            throw new faang.school.postservice.exception.DataAccessException(ex.getMessage());
        }

        authorIdsToBan.forEach(redisUserBanTopicPublisher::publish);
    }

    private void validateContent(CreatePostDto createPostDto) {
        if (createPostDto.content() == null || createPostDto.content().isBlank()) {
            throw new NullPointerException("Content is null or empty");
        }
    }

    private UserDto validateAuthor(Long authorId, Long projectId) {
        boolean isProject = projectId != null;
        boolean isUser = authorId != null;

        if (isProject && isUser) {
            throw new IllegalArgumentException("Only one author must be specified: either the user or the project.");
        }

        if (isProject) {
            if (!existsProject(projectId)) {
                throw new AuthorNotFoundException("Project with ID %d does not exist.".formatted(projectId));
            }

            return null;
        }

        return getUser(authorId);
    }

    private UserDto getUser(Long authorId) {
        try {
            return userServiceClient.getUser(authorId);
        } catch (FeignException e) {
            throw new AuthorNotFoundException(MessageFormat.format("Author with ID {0} does not exist.", authorId));
        }
    }

    private boolean existsProject(Long projectId) {
        try {
            projectServiceClient.getProject(projectId);
            return true;
        } catch (FeignException e) {
            return false;
        }
    }

    private List<UserDto> getUserFollowers(Long authorId) {
        try {
            return userServiceClient.getUserFollowers(authorId);
        } catch (FeignException e) {
            throw new AuthorNotFoundException(MessageFormat.format("Author with ID {0} does not exist.", authorId));
        }
    }
}
