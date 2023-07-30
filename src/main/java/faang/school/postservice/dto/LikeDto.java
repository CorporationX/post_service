package faang.school.postservice.dto;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class LikeDto {

    @NotNull
    @Size(min = 1, message = "Id cannot be 0")
    private final Long id;

    @NotNull
    @Size(min = 1, message = "UserId cannot be 0")
    private final Long userId;

    @NotNull
    private final Comment comment;

    @NotNull
    private final Post post;
}
