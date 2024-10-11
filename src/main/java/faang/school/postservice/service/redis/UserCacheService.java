package faang.school.postservice.service.redis;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.UserMapper;
import faang.school.postservice.model.redis.RedisUser;
import faang.school.postservice.repository.redis.UserCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static faang.school.postservice.converters.CollectionConverter.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCacheService {
    @Value(value = "${spring.data.redis.cache.user_topic.ttl}")
    private long ttl;
    private final UserCacheRepository userCacheRepository;
    private final UserServiceClient userServiceClient;
    private final UserMapper userMapper;

    public void addUser(Long authorId) {
        RedisUser user = findUserById(authorId);
        if (user == null) {
            log.info("User with id {} not found in Cache", authorId);
            UserDto userDto = userServiceClient.getUser(authorId);
            saveUser(userMapper.toRedisEntity(userDto));
            log.info("User with id {} was saved in Cache successfully", authorId);
        } else {
            log.info("User with id {} already exist in Cache", authorId);
        }
    }

    public RedisUser findUserById(Long id) {
        Optional<RedisUser> user = userCacheRepository.findById(id);
        return user.orElseGet(() -> {
            UserDto userDto = userServiceClient.getUser(id);
            return userMapper.toRedisEntity(userDto);
        });
    }

    public List<RedisUser> findUsersByIds(List<Long> ids) {
        List<RedisUser> users = toList(userCacheRepository.findAllById(ids));
        if(users.isEmpty()){
            List<UserDto> usersDtoList = userServiceClient.getUsersByIds(ids);
            users = usersDtoList.stream().map(userMapper::toRedisEntity)
                    .toList();
        } else if (users.size() != ids.size()) {
            List<Long> idsList = new ArrayList<>(ids);
            idsList.removeAll(users.stream().map(RedisUser::getId).toList());
            List<UserDto> usersDtoList = userServiceClient.getUsersByIds(idsList);
            users.addAll(usersDtoList.stream().map(userMapper::toRedisEntity)
                    .toList());
        }
        return users;
    }

    private void saveUser(RedisUser user) {
        user.setTimeToLive(ttl);
        userCacheRepository.save(user);
    }

    public void loadAndSaveAllUsers(){
        log.info("Loading all users from database");
        List<UserDto> allUsers = userServiceClient.getAllUsers();
        List<RedisUser> redisUsers = allUsers.stream().map(userMapper::toRedisEntity).toList();
        userCacheRepository.saveAll(redisUsers);
        log.info("All users from database were saved successfully to the User cache");
    }

}
