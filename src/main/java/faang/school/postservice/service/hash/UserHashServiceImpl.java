package faang.school.postservice.service.hash;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.hash.UserHash;
import faang.school.postservice.repository.UserHashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserHashServiceImpl implements UserHashService{
    private final UserHashRepository userHashRepository;

    @Value("${feed.post.time-to-live}")
    private long ttl;

    @Override
    @Retryable(retryFor = OptimisticLockingFailureException.class,
            maxAttemptsExpression = "${feed.maxAttempts}")
    public void saveAuthor(UserDto userDto) {
        UserHash userHash = new UserHash();

        userHash.setTtl(ttl);
        userHash.setUserDto(userDto);
        userHash.setUserId(userDto.getId());
        userHash.setVersion(1L);

        userHashRepository.save(userHash);
    }
}
