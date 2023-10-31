package faang.school.postservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {

    @NotBlank(message = "Comment content can't be empty or blank")
    @NotNull(message = "Comment content can't be null")
    @Size(max = 4096, message = "Content must be less than 4096 characters")
    private String content;
    @Min(value = 1)
    private long authorId;
    @Min(value = 1)
    private long postId;
    private LocalDateTime createdAt;
}
