package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    @NotBlank(message = "Comment content cannot be empty")
    @Size(max = 4096, message = "Comment content is too long (max 4096)")
    private String content;
    @NotNull
    private Long authorId;
    private List<Long> likesIds;
    private Long postId;
    private Integer likeCount;
    @NotNull(message = "Author must be specified")
    private LocalDateTime createdAt; // Readonly
}
