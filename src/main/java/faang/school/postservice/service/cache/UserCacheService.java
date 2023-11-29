package faang.school.postservice.service.cache;

import faang.school.postservice.dto.redis.cash.UserCache;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.redis.UserCacheMapper;
import faang.school.postservice.repository.redis.UsersCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCacheService {

    private final UsersCacheRepository usersCacheRepository;
    private final UserCacheMapper userCacheMapper;

    @Value("${news_feed.redis.cache.user_ttl}")
    private Long timeout;
    @Value("${news_feed.redis.cache.key_name_user}")
    public String keyName;

    public UserCache get(Long id) {
        String cacheId = keyName + id;
        return usersCacheRepository.get(cacheId, timeout);
    }

    public void save(UserDto userDto) {
        UserCache userCache = userCacheMapper.toCacheDto(userDto);
        String cacheId = keyName + userCache.getId();
        userCache.setId(cacheId);
        usersCacheRepository.save(userCache, timeout);

        log.info("User saved in cache: {}", userCache);
    }

    public void delete(Long id) {
        String cacheId = keyName + id;
        usersCacheRepository.delete(cacheId);
    }

    public void update(UserDto userDto) {
        String cacheId = keyName + userDto.getId();
        UserCache userCache = userCacheMapper.toCacheDto(userDto);
        userCache.setId(cacheId);
        usersCacheRepository.update(userCache, timeout);
    }
}
