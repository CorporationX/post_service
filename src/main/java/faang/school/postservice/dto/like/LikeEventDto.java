package faang.school.postservice.dto.like;

import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public record LikeEventDto(@Positive Long postId,
                           @Positive Long authorId,
                           @Positive Long userExciterId,
                           LocalDateTime createdAt) {
}

