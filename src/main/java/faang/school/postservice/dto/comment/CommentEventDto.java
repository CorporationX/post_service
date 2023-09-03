package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;


@Data
@Validated
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentEventDto {

    @NotNull
    private Long postId;

    @NotNull
    private Long authorId;

    @NotNull
    private Long commentId;
}
