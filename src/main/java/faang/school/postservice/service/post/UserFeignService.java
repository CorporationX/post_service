package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.handler.FeignExceptionHandler;
import feign.FeignException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
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

    public List<UserDto> getUsersByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        try {
            return userServiceClient.getUsersByIds(ids);
        } catch (FeignException ex) {
            log.warn("getUsersByIds failed, ids={}", ids, ex);
            return List.of();
        }
    }
}
