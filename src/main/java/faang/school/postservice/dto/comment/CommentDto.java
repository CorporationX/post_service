package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    @NotBlank
    private Long id;
    @NotBlank
    private Long postId;
    @NotBlank
    private Long authorId;
    @NotBlank
    private String content;

}
