package faang.school.postservice.service.hash;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.hash.UserHash;
import faang.school.postservice.mapper.UserHashMapper;
import faang.school.postservice.repository.hash.UserHashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserHashService {
    private final UserHashRepository userHashRepository;
    private final UserHashMapper userHashMapper;

    @Value("${feed.ttl}")
    private long ttl;

    @Async("taskExecutor")
    @Retryable(retryFor = OptimisticLockingFailureException.class, maxAttemptsExpression = "${feed.retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${feed.retry.maxDelay}"))
    public void saveUserAsync(UserDto userDto, Acknowledgment acknowledgment) {
        boolean exists = userHashRepository.findById(userDto.getId()).isPresent();
        if (!exists) {
            UserHash hash = userHashMapper.toHash(userDto);
            hash.setTtl(ttl);
            userHashRepository.save(hash);
        }
        acknowledgment.acknowledge();
    }

    public void saveUser(UserDto userDto) {
        boolean exists = userHashRepository.findById(userDto.getId()).isPresent();
        if (!exists) {
            UserHash hash = userHashMapper.toHash(userDto);
            hash.setTtl(ttl);
            userHashRepository.save(hash);
        }
    }
}
