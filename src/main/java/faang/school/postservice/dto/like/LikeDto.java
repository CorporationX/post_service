package faang.school.postservice.dto.like;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeDto {

    @NotNull
    @Min(value = 1, message = "ID must be a positive number")
    private Long id;

    @NotNull
    @Min(value = 1, message = "ID must be a positive number")
    private Long userId;

    @NotNull
    @Min(value = 1, message = "ID must be a positive number")
    private Long postId;

    private LocalDateTime likeDate;
}
