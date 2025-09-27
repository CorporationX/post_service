package faang.school.postservice.dto.user;

import java.util.Locale;

public record UserDto(
        Long id,
        String username,
        String email,
        String phone,
        String aboutMe,
        PreferredContact preference,
        Locale locale
) {
    public enum PreferredContact {
        EMAIL, PHONE, TELEGRAM
    }
}
