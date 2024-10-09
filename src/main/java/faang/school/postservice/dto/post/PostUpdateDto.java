package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PostUpdateDto {
    @NotBlank(message = "Text must not be empty!")
    private String content;
}
