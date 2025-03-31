package faang.school.postservice.dto.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public record UserDto(
    Long id,
    String username,
    String email
) {
}
