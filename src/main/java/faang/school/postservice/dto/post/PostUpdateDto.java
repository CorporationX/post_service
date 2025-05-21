package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostUpdateDto {
    @NotBlank(message = "Post content could not be blank")
    private String content;
}