package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class CommentDto {

    private Long id;

    @NonNull
    private Long authorId;

    @NonNull
    private Long postId;

    private LocalDateTime createdAt;
    private int likeCount;

    @NotBlank(message = "Comment text cannot be empty")
    @Size(max = 4096, message = "Comment text cannot be longer than 4096 characters")
    private String content;
}
