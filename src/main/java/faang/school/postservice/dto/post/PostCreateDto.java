package faang.school.postservice.dto.post;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostCreateDto(@NotBlank @Size(min = 5, max = 4096)
                            String content,
                            @Nullable
                            Long projectId
) {
}
