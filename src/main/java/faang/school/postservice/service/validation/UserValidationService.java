package faang.school.postservice.service.validation;


import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.FeignExceptionHandler;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserValidationService {

    private final UserServiceClient userServiceClient;
    private final FeignExceptionHandler feignExceptionHandler;

    public void validateUserExists(Long authorId) {
        try {
            log.info("Try to find user. Sending request to user_service. User ID: {}", authorId);
            userServiceClient.getUser(authorId);
            log.info("User with ID:{} is present", authorId);
        } catch (FeignException e) {
            feignExceptionHandler.handleFeignException(e, authorId);
        }
    }
}
