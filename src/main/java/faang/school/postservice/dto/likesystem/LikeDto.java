package faang.school.postservice.dto.likesystem;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LikeDto(long id, long userId, long commentId, long postId, LocalDateTime createdAt) {

}
