package faang.school.postservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LikeDto {

    private Long id;
    @NotNull(message = "User ID must be specified")
    private Long userId;
    private Long postId;
    private Long commentId;
}
