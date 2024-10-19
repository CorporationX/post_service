package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CommentDto {
    private long id;
    @NotEmpty
    @Size(max = 4096)
    private String content;
    @NotNull
    private Long authorId;
    private List<Long> likeIds;
    private long postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
