package faang.school.postservice.dto.resource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record ResourceRequest(
        @Positive(message = "Id must be a positive number")
        Long id,

        @NotBlank(message = "Key must not be empty")
        String key,

        @Positive(message = "File size must be positive")
        long size,

        @NotBlank(message = "Name must not be empty")
        String name,

        @NotBlank(message = "Type must not be empty")
        String type,

        @NotNull(message = "PostId must not be null")
        @Positive(message = "PostId must be a positive number")
        Long postId
) {
}