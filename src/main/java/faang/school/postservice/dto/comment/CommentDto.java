package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    @NotNull(message = "CommentId can not be null")
    @Positive(message = "CommentId should be positive")
    private Long id;

    @NotBlank(message = "Comment should not be blank")
    @Size(max = 4096, message = "Comment can not be greater than 4096 symbols.")
    private String content;

    @NotNull(message = "AuthorId shouldn't be null")
    @Positive(message = "AuthorId should be positive")
    private Long authorId;

    @PastOrPresent(message = "Comment can not be declared in future")
    private LocalDateTime createdAt;
}
