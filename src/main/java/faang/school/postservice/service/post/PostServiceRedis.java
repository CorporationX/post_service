package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.mapper.user.UserEventMapper;
import faang.school.postservice.model.event.PostEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.event.UserEvent;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostServiceRedis {
    private final RedisPostRepository redisPostRepository;
    private final RedisUserRepository redisUserRepository;
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final UserEventMapper userEventMapper;

    public void save(PostEvent postEvent) {
        redisPostRepository.save(postEvent);
        log.debug("Post {} added to Redis cache", postEvent.getId());

        List<Long> followersId = postRepository.findFollowersByAuthorId(postEvent.getAuthorId());
        postEvent.setFollowersId(followersId);

        UserDto author = userServiceClient.getUser(postEvent.getAuthorId());
        UserEvent userEvent =  userEventMapper.toEvent(author);
        redisUserRepository.save(userEvent);
        log.debug("Author with id {} of the post {} added to Redis cache", author.getId(), postEvent.getId());
    }
}
