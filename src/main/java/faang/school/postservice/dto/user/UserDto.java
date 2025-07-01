package faang.school.postservice.dto.user;

import java.util.List;

public record UserDto(
    Long id,
    String username,
    String email,
    String preference,
    List<ContactDto> contacts
) {
}
