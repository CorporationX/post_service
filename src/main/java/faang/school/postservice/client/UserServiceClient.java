package faang.school.postservice.client;

import faang.school.postservice.config.resilience4j.Resilience4jProperties;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.user.UserForNotificationDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}/api/v1/users")
public interface UserServiceClient {

    @GetMapping("/{userId}")
    UserDto getUserById(@PathVariable long userId);

    @PostMapping
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @GetMapping("/notification/{userId}")
    @Retry(name = Resilience4jProperties.DEFAULT_RETRY_CONFIG_NAME)
    @CircuitBreaker(name = Resilience4jProperties.DEFAULT_RETRY_CONFIG_NAME)
    UserForNotificationDto getUserForNotificationById(@Positive @PathVariable long userId);
}
