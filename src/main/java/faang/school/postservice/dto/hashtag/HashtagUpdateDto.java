package faang.school.postservice.dto.hashtag;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record HashtagUpdateDto(
        long id,
        @NotBlank(message = "Название хештега не должно быть пустым")
        String name
) {
}