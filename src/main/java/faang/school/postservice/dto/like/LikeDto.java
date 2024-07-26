package faang.school.postservice.dto.like;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikeDto {

    @NotNull(message = "Id must be provided")
    private Long id;

    @Min(value = 0, message = "User ID must be positive")
    @NotNull(message = "User ID must be provided")
    private long userId;

    private long commentId;
    private long postId;
}