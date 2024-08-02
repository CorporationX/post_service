package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    private Long id;

    @Size(max = 4096)
    @NotBlank
    private String content;

    @NotNull
    @Positive
    private Long authorId;

    @NotNull
    @Positive
    private Long postId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
