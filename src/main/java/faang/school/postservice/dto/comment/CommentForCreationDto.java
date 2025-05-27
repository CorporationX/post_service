package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentForCreationDto extends CommentDto{
    @NotNull(message = "Post ID cannot be null")
    private Long postId;

    @NotNull(message = "Content cannot be null")
    @Size(min = 1, max = 4096, message = "Content cannot be empty")
    @NotBlank(message = "Content cannot be blank")
    private String content;
}
