package faang.school.postservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class LikeDto {
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private Long commentId;

    @NotNull
    private Long postId;
}
