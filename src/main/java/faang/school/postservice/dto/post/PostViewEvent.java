package faang.school.postservice.dto.post;

import java.time.LocalDateTime;

public record PostViewEvent(long postId, LocalDateTime date) {
}
