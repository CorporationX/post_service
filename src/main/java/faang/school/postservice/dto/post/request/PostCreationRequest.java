package faang.school.postservice.dto.post.request;

import faang.school.postservice.validation.OnlyPostCreator;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@OnlyPostCreator
public record PostCreationRequest(
        @Positive(message = "authorId must be greater than 0")
        Long authorId,

        @Positive(message = "projectId must be greater than 0")
        Long projectId,

        @NotBlank(message = "Post content can't be null or empty")
        String content,

        @Future(message = "Must contain a date that has not yet arrived")
        LocalDateTime scheduledAt
) {
}
