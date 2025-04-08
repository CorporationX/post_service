package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentUpdateDto {

    @NotBlank
    private String content;

    @NotNull
    @Min(1)
    private Long authorId;
}
