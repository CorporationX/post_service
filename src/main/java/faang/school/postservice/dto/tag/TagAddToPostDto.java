package faang.school.postservice.dto.tag;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record TagAddToPostDto(
        @NotNull List<Long> tagsId,
        @NotNull @Min(1) Long userId
) {
}
