package faang.school.postservice.dto.post;

import faang.school.postservice.dto.like.LikeDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PostDto(
        @Min(value = 1, message = "ID must be a positive number")
        long id,
        @NotNull(message = "Author ID cannot be null")
        Long authorId,
        @NotNull(message = "Likes list cannot be null")
        List<LikeDto> likes) {
}
