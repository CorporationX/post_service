package faang.school.postservice.dto.comment;

import faang.school.postservice.validation.dto.DtoValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CommentDto {
    private Long id;
    @NotBlank(message = DtoValidation.COMMENT_DTO_CONTENT_MISSING)
    private String content;
    @NotNull(message = DtoValidation.COMMENT_DTO_AUTHOR_ID_MISSING)
    private long authorId;
    private List<Long> likesId;
    @NotNull(message = DtoValidation.COMMENT_DTO_POST_ID_MISSING)
    private long postId;
}
