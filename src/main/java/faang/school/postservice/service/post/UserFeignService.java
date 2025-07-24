package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.handler.FeignExceptionHandler;
import feign.FeignException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFeignService {

    private final UserServiceClient userServiceClient;
    private final FeignExceptionHandler feignExceptionHandler;

    public UserDto getUserOrFail(@NonNull Long authorId) {
        try {
            return userServiceClient.getUser(authorId);
        } catch (FeignException feignException) {
            throw feignExceptionHandler.handleFeignException(
                    feignException,
                    "User",
                    authorId
            );
        }
    }
}
