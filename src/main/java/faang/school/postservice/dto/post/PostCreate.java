package faang.school.postservice.dto.post;

import lombok.Builder;

import java.util.List;

@Builder
public record PostCreate(Long postId, List<Long> userIds) {
}
