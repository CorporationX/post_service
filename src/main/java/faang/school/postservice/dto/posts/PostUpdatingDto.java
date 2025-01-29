package faang.school.postservice.dto.posts;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record PostUpdatingDto(
        @Positive Long postId,
        @NotBlank String updatingContent
) {}
