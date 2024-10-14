package faang.school.postservice.dto.like;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public abstract class AbstractLikeEvent {
    @Positive
    protected final Long likedEntityId;

    @Positive
    protected final Long authorId;

    @Positive
    protected final Long userExciterId;

    protected final LocalDateTime createdAt = LocalDateTime.now();
}

