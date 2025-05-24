package faang.school.postservice.component;

import faang.school.postservice.dto.PostResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.repository.RedisPostRepository;
import faang.school.postservice.repository.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisRepositoryCoordinator {

    private final RedisUserRepository redisUserRepository;
    private final RedisPostRepository redisPostRepository;

    public void savePostToRedis(PostResponseDto postDto) {
        redisPostRepository.savePost(postDto);
    }

    public void saveUserToRedis(UserDto userDto) {
        redisUserRepository.saveUser(userDto);
    }
}
