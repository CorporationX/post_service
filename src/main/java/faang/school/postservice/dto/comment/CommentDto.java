package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    private Long id;
    @NotEmpty
    @Size(max = 4096)
    private String content;
    @NotNull
    private Long authorId;
    @NotNull
    private Long postId;
    private LocalDateTime createdAt;
    private Long likes;
}
