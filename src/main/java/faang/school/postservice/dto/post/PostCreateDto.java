package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PostCreateDto {

    @NotBlank(message = "Post content can't be empty")
    @Size(min = 3, message = "Size of post content must contains minimum 3 characters")
    private String content;
    private Long projectId;
    private Long authorId;
}
