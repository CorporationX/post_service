package faang.school.postservice.dto.user;

import lombok.Builder;

@Builder
public record UserDto(
        Long id,
        String username,
        String email,
        String phone,
        String aboutMe,
        String locale,
        String preference
) {
}
