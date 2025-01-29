package faang.school.postservice.dto.posts;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Builder
public record PostCreatingRequest(@NotBlank String content, @Positive Long authorId, @Positive Long projectId) {
}