package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentPublishedEvent implements Serializable {
    private final long id;

    @NotBlank
    @Size(max = 4096, message = "The allowed maximum length is 4096 characters.")
    private final String content;

    @NotNull
    private final long authorId;

    @NotNull
    private final long postId;

    private LocalDateTime date;
}
