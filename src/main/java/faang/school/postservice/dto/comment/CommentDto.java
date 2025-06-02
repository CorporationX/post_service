package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    @NotNull
    private Long id;

    @NotBlank
    @Size(max = 4096)
    private String content;

    @NotNull
    private Long authorId;

    @NotNull
    private Long postId;
}
