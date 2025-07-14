package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    @Min(value = 1, message = "ID must be a positive number")
    private Long id;

    @NotBlank(message = "Comment content must not be blank")
    @Size(max = 4096, message = "Comment must be no longer than 4096 characters")
    private String content;

    @NotNull(message = "Author ID must not be null")
    @Min(value = 1, message = "Author ID must be a positive number")
    private Long authorId;

    @NotNull(message = "Post ID must not be null")
    @Min(value = 1, message = "Post ID must be a positive number")
    private Long postId;
}
