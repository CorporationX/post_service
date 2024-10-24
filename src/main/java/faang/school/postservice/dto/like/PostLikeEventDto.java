package faang.school.postservice.dto.like;

import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PostLikeEventDto extends AbstractLikeEvent {
    private final long postId;

    @Builder
    public PostLikeEventDto(@Positive Long postId,
                            @Positive Long authorId,
                            @Positive Long userExciterId) {
        super(authorId, userExciterId);
        this.postId = postId;
    }

}