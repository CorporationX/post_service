package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.mapper.user.UserMapper;
import faang.school.postservice.repository.redis.UserRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserServiceClient userServiceClient;
    private final UserRedisRepository userRedisRepository;
    private final UserMapper userMapper;
}
