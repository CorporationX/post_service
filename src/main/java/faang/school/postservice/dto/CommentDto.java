package faang.school.postservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
    private Long id;

    @NotBlank(message = "Content cannot be blank")
    @Size(max = 4096, message = "Content length must be less than or equal to 4096 characters")
    private String content;

    @NotNull(message = "AuthorId cannot be null")
    private Long authorId;
    private LocalDateTime createdAt;
    private Long likeCount;
}
