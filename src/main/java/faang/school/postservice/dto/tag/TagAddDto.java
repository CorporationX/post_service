package faang.school.postservice.dto.tag;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record TagAddDto(
        @NotNull List<TagDto> tagDtos,
        @NotNull @Min(1) Long postId,
        @NotNull @Min(1) Long userId
) {
}
