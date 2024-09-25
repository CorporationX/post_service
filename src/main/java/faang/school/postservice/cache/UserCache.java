package faang.school.postservice.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.user.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserCache extends AbstractCache {

    private final HashOperations<String, String, String> hashOperations;
    private final ObjectMapper objectMapper;

    public UserCache(RedisTemplate<String, Object> redisTemplate,
                     HashOperations<String, String, String> hashOperations,
                     ObjectMapper objectMapper) {
        super(redisTemplate);
        this.hashOperations = hashOperations;
        this.objectMapper = objectMapper;
    }

    @Value("${spring.data.redis.properties.ttl}")
    private int ttl;

    @Value("${spring.data.redis.keys.user}")
    private String usersKeyName;

    @Retryable(retryFor = {OptimisticLockingFailureException.class}, maxAttempts = 5, backoff = @Backoff(delay = 100, multiplier = 3))
    public void save(UserDto userDto) {
        try {
            String jsonValue = objectMapper.writeValueAsString(userDto);
            String userKey = prepareUserKey(userDto.getId());

            Boolean success = executeTransactionalOperation(userKey, (connection -> {
                connection.hashCommands().hSet(usersKeyName.getBytes(), userKey.getBytes(), jsonValue.getBytes());
                connection.keyCommands().expire(userKey.getBytes(), ttl);
                return true;
            }));

            if (success == null || !success) {
                throw new OptimisticLockingFailureException(String.format("Unsuccessfully trying to save user %s to users cache", userDto.getId()));
            }

            log.info(String.format("Successfully trying to save user %s to users cache", userDto.getId()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public List<UserDto> getByIdList(List<Long> usersIds) {
        List<String> preparedPostKeys = usersIds.stream()
                .map(this::prepareUserKey)
                .toList();

        List<String> users = hashOperations.multiGet(usersKeyName, preparedPostKeys);
        return users.stream()
                .map(this::readToUserDto)
                .toList();
    }

    private String prepareUserKey(Long postId) {
        return String.format("%s_%s", usersKeyName, postId);
    }

    public UserDto readToUserDto(String value) {
        try {
            return objectMapper.readValue(value, UserDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
