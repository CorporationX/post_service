package faang.school.postservice.service.redis;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.redis.UserForCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserForFeedService {

    private final UserCacheService userCacheService;
    private final UserServiceClient userServiceClient;

    public String getUserName(Long userId) {
        String userName = "";
        Optional<UserForCache> userFromCacheOptional = userCacheService.getUserFromCache(userId);
        if(userFromCacheOptional.isPresent()){
            UserForCache userFromCache = userFromCacheOptional.get();
            userName = userFromCache.getUsername();
        } else {
            UserDto userDto = userServiceClient.getUser(userId);
            userName = userDto.getUsername();
        }
        return userName;
    }
}
