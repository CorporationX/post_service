package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.UserServiceUnavailableException;
import faang.school.postservice.repository.UserCacheRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCacheService {
    private final UserCacheRepository repository;
    private final UserServiceClient client;

    @CircuitBreaker(name = "userService", fallbackMethod = "fallback")
    public void cacheAuthor(long authorId) {
        repository.get(authorId).orElseGet(() -> {
            UserDto dto = client.getUser(authorId);
            repository.save(authorId, dto);

            log.debug("Cached author {}", authorId);
            return dto;
        });
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackGet")
    public UserDto getAuthor(long authorId) {
        return repository.get(authorId)
                .orElseGet(() -> {
                            UserDto dto = client.getUser(authorId);
                            repository.save(authorId, dto);
                            return dto;
                        }
                );
    }

    private void fallback(long authorId, Throwable ex) {
        log.warn("Cannot cache author {}", authorId, ex);
    }

    private UserDto fallbackGet(long authorId, Throwable ex) {
        log.error("User service unavailable for authorId={}", authorId, ex);
        throw new UserServiceUnavailableException("User service unavailable", ex);
    }
}