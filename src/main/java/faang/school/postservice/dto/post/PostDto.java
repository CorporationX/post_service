package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostDto {
    @NotNull
    private String content;
    private Long authorId;
    private Long projectId;
}
