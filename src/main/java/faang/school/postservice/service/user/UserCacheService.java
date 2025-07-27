package faang.school.postservice.service.user;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.cache.UserCacheModel;
import faang.school.postservice.repository.UserCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserCacheService {
    private final UserCacheRepository userCacheRepository;

    @Value("${app.cache.user.ttl}")
    private long ttl;

    public void saveUser(UserDto userDto) {
        UserCacheModel userCacheModel = buildUserModel(userDto);

        UserCacheModel savedUser = userCacheRepository.save(userCacheModel);
        log.info("User {} has been saved in cache", savedUser);
    }

    private UserCacheModel buildUserModel(UserDto userDto) {
        return UserCacheModel.builder()
                .key(String.valueOf(userDto.getId()))
                .ttl(ttl)
                .id(userDto.getId())
                .username(userDto.getUsername())
                .build();
    }
}
