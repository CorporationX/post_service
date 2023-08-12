package faang.school.postservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class LikeDto {

    @NotNull
    private final Long id;

    @NotNull
    private final Long userId;

    @NotNull
    private final Long commentId;

    @NotNull
    private final Long postId;
}
