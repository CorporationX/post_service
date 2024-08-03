package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private long id;
    @NotBlank
    @Size(max = 4096, message = "Content must be less than 4096 characters")
    private String content;
    @Min(value = 1, message = "Author id should not be less than 1")
    private long authorId;
    @Min(value = 1, message = "Post id should not be less than 1")
    private long postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
