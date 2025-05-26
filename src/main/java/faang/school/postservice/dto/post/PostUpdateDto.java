package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PostUpdateDto {
    @NotBlank(message = "Post content could not be blank")
    private String content;
}