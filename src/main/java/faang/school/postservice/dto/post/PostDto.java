package faang.school.postservice.dto.post;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostDto {
    @Positive(message = "Post ID must be a positive number.")
    Long id;

    @NotBlank(message = "Content must not be blank.")
    @Size(min = 10, max = 5000, message = "Content length must be between 10 and 5000 characters.")
    String content;

    @Positive(message = "Author ID must be a positive number.")
    Long authorId;

    @Positive(message = "Project ID must be a positive number.")
    Long projectId;

    @NotNull(message = "Published status must not be null.")
    Boolean published;

    Boolean deleted;

    @FutureOrPresent(message = "Published date must be in the present or future.")
    LocalDateTime publishedAt;

    @FutureOrPresent(message = "Scheduled date must be in the present or future.")
    LocalDateTime scheduledAt;

    @NotNull(message = "Creation date must not be null.")
    @PastOrPresent(message = "Creation date must be in the past or present.")
    LocalDateTime createdAt;

    @PastOrPresent(message = "Updated date must be in the past or present.")
    LocalDateTime updatedAt;
}
