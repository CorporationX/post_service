package faang.school.postservice.dto.like;

import jakarta.validation.constraints.Positive;
import lombok.Builder;


public class PostLikeEventDto extends AbstractLikeEvent {

    @Builder
    public PostLikeEventDto(@Positive Long likedEntityId,
                            @Positive Long authorId,
                            @Positive Long userExciterId) {
        super(likedEntityId, authorId, userExciterId);
    }

    public long getPostId() {
        return getLikedEntityId();
    }
}
