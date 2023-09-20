package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PostDto {
    @NotNull
    private String content;
    private Long authorId;
    private Long projectId;
    private List<Long> likes;
}
