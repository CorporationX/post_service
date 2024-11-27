package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class CommentDto {

    private final Long id;

    @NotBlank
    @Size(max = 4096, message = "The allowed maximum length is 4096 characters.")
    private final String content;

    @NotNull
    private final Long authorId;

    @NotNull
    private final Long postId;
}
