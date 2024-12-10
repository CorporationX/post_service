package faang.school.postservice.dto.like;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikePostCreateDto {

    @NotNull(message = "Post ID cannot be null")
    private Long postId;

    @NotNull(message = "Liked user ID cannot be null")
    private Long likedUserId;
}
