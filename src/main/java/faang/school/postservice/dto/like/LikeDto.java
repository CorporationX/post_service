package faang.school.postservice.dto.like;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeDto {

    @Positive(message = "Field cannot be less then zero")
    private Long id;

    @NotNull(message = "Field cannot be empty")
    @NotBlank(message = "Field cannot be empty")
    @Positive(message = "Field cannot be less then zero")
    private Long userId;

    @Positive(message = "Field cannot be less then zero")
    private Long commentId;

    @NotNull(message = "Field cannot be empty")
    @NotBlank(message = "Field cannot be empty")
    @Positive(message = "Field cannot be less then zero")
    private Long postId;

    @Positive(message = "Field cannot be less then zero")
    private LocalDateTime createdAt;
}
