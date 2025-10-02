package faang.school.postservice.dto.user;

import lombok.Builder;

@Builder
public record UserViewDto(
    Long id,
    String username,
    String email
) {
}
