package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.kafka.PostPublishedDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final PostService postService;
    private final UserServiceClient userServiceClient;
    private final RedisTemplate<String, Object> redisTemplate;

        //0. Получить PostPublishedDto
    //1. Достать фид по ID пользователя из Redis
    //2. Превратить Feed в DTO.
    //3. Проверить заполненность фида, если заполнен(удалить первый) если нет вставить в конец.
    public void updateFeed(PostPublishedDto postPublishedDto) {
        List<Long> followers = userServiceClient.getFollowersIds(postPublishedDto.getAuthorId());
        for (Long id : followers) {
            ;
        }
    }

}
