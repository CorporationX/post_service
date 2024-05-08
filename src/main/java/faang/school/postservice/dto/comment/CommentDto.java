package faang.school.postservice.dto.comment;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class CommentDto {
    private Long id;
    @NotNull
    private Long authorId;
    @Size(min = 1, max = 4096, message = "Comment should be between 1 and 4096 symbols")
    private String content;
    private List<Long> likesIds;
    @NotNull
    private Long postId;
}
