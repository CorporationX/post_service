package faang.school.postservice.event;

import lombok.Builder;

public record LikeEvent(long postAuthorId,
                        long likeAuthorId,
                        long postId) {

}
