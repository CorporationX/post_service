package faang.school.postservice.dto.resource;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ResourceResponse(
        @Positive(message = "Id must be a positive number")
        Long id,

        @NotBlank(message = "Key must not be empty")
        String key,

        long size,

        LocalDateTime createdAt,

        @NotBlank(message = "Name must not be empty")
        String name,

        @NotBlank(message = "Type must not be empty")
        String type,

        @Positive(message = "PostId must be a positive number")
        Long postId
) {}