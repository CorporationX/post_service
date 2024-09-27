package faang.school.postservice.dto.post;

import faang.school.postservice.dto.like.LikeDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PostDto(
        @Min(value = 1, message = "ID must be a positive number")
        long id,
        @NotNull(message = "Author ID cannot be null")
        Long authorId,
        @NotBlank(message = "The content is empty")
        @Size(min = 1, max = 4096, message = "The content size should be between 1 and 4096 characters")
        String content,
        @NotNull(message = "Likes list cannot be null")
        List<LikeDto> likes,
        Long projectId,
        boolean published,
        boolean deleted) {
}
