package faang.school.postservice.dto.tag;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record TagRemoveDto(
        @NotNull List<Long> tagsId,
        @NotNull Long userId
) {
}
