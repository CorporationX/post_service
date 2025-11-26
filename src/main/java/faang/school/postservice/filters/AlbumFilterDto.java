package faang.school.postservice.filters;

import java.time.LocalDateTime;

public record AlbumFilterDto(
        String title,
        String description,
        LocalDateTime fromDate
) {
}
