package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PostDto {
    private Long id;
    @NotBlank(message = "content cannot be null!")
    private String content;

    private Long authorId;

    private Long projectId;

    private boolean published;
}
