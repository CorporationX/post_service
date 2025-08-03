package faang.school.postservice.dto.user;

import java.io.Serializable;

public record UserCacheDto(
        Long id,
        String username,
        String email
) implements Serializable {
}
