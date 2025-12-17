package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    private Long id;
    private Long authorId;

    @NotNull
    @NotBlank
    @Size(max = 4096, message = "Content must not exceed 4096 characters")
    private String content;

    @NotNull
    private Long postId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


