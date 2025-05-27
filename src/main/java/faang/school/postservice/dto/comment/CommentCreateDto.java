package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentCreateDto {

    @NotBlank
    private Long postId;
    @NotBlank
    private Long authorId;
    @NotBlank
    private String content;
}
