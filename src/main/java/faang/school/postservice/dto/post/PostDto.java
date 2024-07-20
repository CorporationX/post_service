package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class PostDto {
    private Long id;
    @NotEmpty(message = "content cannot be null!")
    private String content;

    private Long authorId;

    private Long projectId;
}
