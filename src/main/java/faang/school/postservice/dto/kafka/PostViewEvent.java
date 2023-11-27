package faang.school.postservice.dto.kafka;

import lombok.Builder;

@Builder
public record PostViewEvent(Long postId) {
}
