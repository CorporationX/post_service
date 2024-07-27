package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    private Long id;

    @Size(min = 1, max = 4096)
    private String content;

    @NotNull
    private Long authorId;

    @NotNull
    private Long postId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
