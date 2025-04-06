package faang.school.postservice.dto;

import org.springframework.lang.Nullable;

public record AlbumFilterDto(
        @Nullable String titlePattern
) {
}
