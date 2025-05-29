package faang.school.postservice.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LikeDto(
        Long id,
        Long userId,
        LocalDateTime createdAt
) {}
