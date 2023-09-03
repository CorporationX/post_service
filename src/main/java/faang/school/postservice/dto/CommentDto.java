package faang.school.postservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    @NotBlank(message = "The comment text cannot be empty")
    @Size(max = 4096, message = "The comment text is too long")
    private String content;
    private Long authorId;
    private Long postId;

}
