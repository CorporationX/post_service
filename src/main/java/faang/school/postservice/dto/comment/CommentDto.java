package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    Long id;
    @NotEmpty
    @Size(max = 4096)
    String content;
    @NotNull
    Long authorId;
    @NotNull
    Long postId;
    LocalDateTime createdAt;
    Long likes;
}
