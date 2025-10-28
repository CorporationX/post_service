package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostUpdateDto(@NotBlank @Size(min = 5, max = 4096) String content) {
}
