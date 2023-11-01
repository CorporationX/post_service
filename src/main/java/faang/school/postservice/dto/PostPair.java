package faang.school.postservice.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostPair(Long postId, LocalDateTime publishedAt) {
}
