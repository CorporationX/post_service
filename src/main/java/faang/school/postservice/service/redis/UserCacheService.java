package faang.school.postservice.service.redis;

import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.model.redis.User;
import faang.school.postservice.repository.redis.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static faang.school.postservice.converters.CollectionConverter.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCacheService {
    private final UserRepository userRepository;

    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public List<User> findUsersByIds(List<Long> ids) {
        return toList(userRepository.findAllById(ids));
    }
}
