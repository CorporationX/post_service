package faang.school.postservice.dto.cache;

import lombok.Builder;

@Builder
public record UserCacheDto(
        Long id,
        String name) {
}
