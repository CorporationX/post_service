package faang.school.postservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    @NotNull
    @Size(min = 1, max = 4096, message = "Content must not exceed 4096 characters")
    private String content;
    @NotNull
    private Long authorId;
    @NotNull
    private Long postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int likeCount;
}