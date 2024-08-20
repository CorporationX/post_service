package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdatePostDto {
    @NotBlank(message = "Text must not be empty!")
    private String content;
}
