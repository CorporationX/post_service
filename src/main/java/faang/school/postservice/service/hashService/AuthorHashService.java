package faang.school.postservice.service.hashService;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.hash.AuthorHash;
import faang.school.postservice.dto.hash.AuthorType;
import faang.school.postservice.repository.redis.AuthorRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@EnableRetry
public class AuthorHashService {

    private final AuthorRedisRepository authorRepository;
    private final UserServiceClient userServiceClient;

    @Value("${spring.data.redis.time_to_live}")
    private long timeToLive;

    @Async("executor")
    @Retryable(retryFor = OptimisticLockingFailureException.class,
            maxAttemptsExpression = "${spring.data.redis.retry_max_attempts}")
    public void saveAuthor(long authorId, AuthorType authorType) {
        AuthorHash authorHash = AuthorHash.builder()
                .authorId(authorId)
                .authorType(authorType)
                .userDto(userServiceClient.getUser(authorId))
                .timeToLive(timeToLive)
                .build();
        authorRepository.saveInRedis(authorHash);
    }
}
