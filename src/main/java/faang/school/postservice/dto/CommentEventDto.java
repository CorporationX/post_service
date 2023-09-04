package faang.school.postservice.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentEventDto {
    @NotNull(message = "Post can't be empty")
    private long postId;

    @NotNull(message = "Author can't be empty")
    private long authorId;

    @NotNull(message = "Comment can't be empty")
    private long commentId;

    private LocalDateTime date;
}