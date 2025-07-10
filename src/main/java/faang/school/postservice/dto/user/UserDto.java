package faang.school.postservice.dto.user;

import java.util.Locale;

public record UserDto(
    Long id,
    String username,
    String email,
    String phone,
    Locale locale
) {
}
