package faang.school.postservice.dto.like;

import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikeDto {
    private Long likeId;

    @NotNull
    private Long userId;
}
