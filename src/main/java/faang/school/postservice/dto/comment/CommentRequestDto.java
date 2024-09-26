package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {
    private final static String EMPTY_CONTENT = "content must not be empty";
    private final static String MAX_SIZE_CONTENT = "content size must be no longer than 4096 symbols";

    @NotBlank(message = EMPTY_CONTENT)
    @Length(max = 4096, message = MAX_SIZE_CONTENT)
    private String content;
}
