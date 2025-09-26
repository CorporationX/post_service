package faang.school.postservice.dto.user;

import lombok.Builder;

import java.util.List;

@Builder
public record UserDto(
    Long id,
    String username,
    String email,
    List<Long> followersIds
) {
}
