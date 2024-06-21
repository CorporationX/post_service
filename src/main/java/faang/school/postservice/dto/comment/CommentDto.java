package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {

    private Long id;

    @NotNull(message = "content shouldn't be null")
    @NotBlank(message = "description should not be blank")
    @Size(max = 4096)
    private String content;

    @NotNull(message = "authorId shouldn't be null")
    private Long authorId;

    private LocalDateTime createdAt;
}
