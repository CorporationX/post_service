package faang.school.postservice.dto.album;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AlbumDto(
        Long id,
        @NotBlank(message = "Title is required")
        String title,
        String description,
        @NotNull(message = "Author id is required")
        Long authorId,
        List<Long> postIds
) {
}