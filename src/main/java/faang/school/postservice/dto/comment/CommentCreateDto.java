package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentCreateDto {

    private final static String EMPTY_CONTENT = "content must not be empty";
    private final static String MAX_SIZE_CONTENT = "content size must be no longer than 4096 symbols";
    private final static String AUTHOR_ID_NOT_NULL = "authorId must not be null";

    @NotBlank(message = EMPTY_CONTENT)
    @Size(max = 4096, message = MAX_SIZE_CONTENT)
    private String content;
    @NotNull(message = AUTHOR_ID_NOT_NULL)
    private Long authorId;
}
