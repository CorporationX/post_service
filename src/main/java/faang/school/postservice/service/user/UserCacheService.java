package faang.school.postservice.service.user;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.cache.UserCacheEntity;
import faang.school.postservice.repository.cache.UserCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserCacheService {

    private final UserCacheRepository userCacheRepository;

    @Value("${spring.data.redis.cache.user.ttl}")
    private long ttl;

    public void saveUserInCache(UserDto userDto) {
        UserCacheEntity userCacheEntity = buildUserCacheEntity(userDto);

        UserCacheEntity savedUser = userCacheRepository.save(userCacheEntity);
        log.info("User {} was saved to cache", savedUser.getId());
    }

    private UserCacheEntity buildUserCacheEntity(UserDto userDto) {
        return UserCacheEntity.builder()
                .id(userDto.getId())
                .ttl(ttl)
                .userName(userDto.getUsername())
                .build();
    }

}
