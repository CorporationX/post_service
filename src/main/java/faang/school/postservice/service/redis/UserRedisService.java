package faang.school.postservice.service.redis;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.user.UserMapper;
import faang.school.postservice.repository.redis.UserRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserRedisService {

    private final UserRedisRepository userRedisRepository;
    private final UserMapper userMapper;


    public void saveUser(UserDto userDto){
        userRedisRepository.saveAuthor(userMapper.toRedisModel(userDto));
    }
}
