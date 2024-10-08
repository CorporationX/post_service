package faang.school.postservice.client;

import faang.school.postservice.dto.user.UserDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}")
@Validated
public interface UserServiceClient {

    @GetMapping("/users/{userId}")
    UserDto getUser(@PathVariable @NotNull(message = "Поле не может отсутствовать!")
                    @Min(value = 1,
                            message = "Поле должно быть 1 или более.") long userId);

    @PostMapping("/users")
    List<UserDto> getUsersByIds(@RequestBody @NotEmpty(message = "Список не должен быть пустым.")
                                List<Long> ids);

    @GetMapping("/users")
    List<UserDto> getAllUsers();
}