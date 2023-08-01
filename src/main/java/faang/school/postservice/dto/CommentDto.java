package faang.school.postservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

@Value
@Builder
public class CommentDto implements Serializable {

    long id;

    @Size(min = 1, max = 4096, message = "Comment content cannot exceed 4096 characters")
    @NotBlank(message = "Comment content cannot be empty")
    String content;

    @NotNull(message = "Author ID cannot be null")
    Long authorId;

    @NotNull(message = "Post ID cannot be null")
    Long postId;

    LocalDateTime createdAt;
}