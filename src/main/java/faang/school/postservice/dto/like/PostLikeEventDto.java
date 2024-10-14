package faang.school.postservice.dto.like;

import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public class PostLikeEventDto extends AbstractLikeEvent {

    public PostLikeEventDto(@Positive Long likedEntityId,
                     @Positive Long authorId,
                     @Positive Long userExciterId) {
        super(likedEntityId, authorId, userExciterId);
    }

    public long getPostId() {
        return getLikedEntityId();
    }
}
