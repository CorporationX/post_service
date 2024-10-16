package faang.school.postservice.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeDto {
    private Long id;
    @NotNull(message = "user ID must be provided")
    private Long userId;
    private Long postId;
    private Long commentId;
}

