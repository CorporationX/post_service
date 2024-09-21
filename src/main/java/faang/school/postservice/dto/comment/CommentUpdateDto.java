package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentUpdateDto {

    private final static String EMPTY_CONTENT = "content must not be empty";
    private final static String MAX_SIZE_CONTENT = "content size must be no longer than 4096 symbols";

//    private Long id;
    @NotBlank(message = EMPTY_CONTENT)
    @Size(max = 4096, message = MAX_SIZE_CONTENT)
    private String content;
}
