package faang.school.postservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
