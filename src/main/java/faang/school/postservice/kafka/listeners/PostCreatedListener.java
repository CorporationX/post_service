package faang.school.postservice.kafka.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.dto.post.PostOutputDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.kafka.events.PostCreatedEvent;
import faang.school.postservice.redis.cache.model.RedisPost;
import faang.school.postservice.redis.cache.model.RedisUser;
import faang.school.postservice.redis.cache.service.RedisPostService;
import faang.school.postservice.redis.cache.service.RedisUserService;
import faang.school.postservice.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PostCreatedListener extends AbstractEventListener<PostCreatedEvent> {

    @Autowired
    public PostCreatedListener(ObjectMapper objectMapper,
                               PostService postService,
                               RedisProperties redisProperties,
                               RedisPostService redisPostService,
                               RedisUserService redisUserService, PostService postService1) {
        super(objectMapper, postService, redisProperties, redisPostService, redisUserService);
    }

    @Override
    protected Class<PostCreatedEvent> getEventClass() {
        return PostCreatedEvent.class;
    }

    @Override
    protected void processEvent(PostCreatedEvent event) {
        PostOutputDto post = postService.getPostById(event.getPostId());
        List<UserDto> subscribers = event.getFollowers();
        List<Long> subscriberIds = subscribers.stream().map(UserDto::id).toList();
        RedisPost postToCache = RedisPost.builder()
                .id(post.getId())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .authorId(post.getAuthorId())
                .content(post.getContent())
                .projectId(post.getProjectId())
                .commentIds(post.getCommentIds())
                .timeToLive(redisProperties.getTimeToLive())
                .likeIds(post.getLikeIds())
                .publishedAt(post.getPublishedAt())
                .redisUsersSubscribers(subscriberIds)
                .build();

        redisPostService.savePost(postToCache);

        for (Long id : subscriberIds) {
            RedisUser redisUser = redisUserService.findbyId(id);
            if (redisUser == null) {
                RedisUser redisUserToCreate = new RedisUser();
                redisUserToCreate.setTimeToLive(redisProperties.getTimeToLive());
                redisUserToCreate.setUserId(id);
                redisUserToCreate.setPostsSubscribedTo(new ArrayList<>());
                redisUserToCreate.getPostsSubscribedTo().add(postToCache.getId());
                redisUserService.saveUser(redisUserToCreate);
            } else {
                redisUser.getPostsSubscribedTo().add(postToCache.getId());
            }
        }
    }

    @KafkaListener(topics = "post.created", groupId = "event-listeners")
    public void listen(String message) {
        super.handle(message);
    }
}
