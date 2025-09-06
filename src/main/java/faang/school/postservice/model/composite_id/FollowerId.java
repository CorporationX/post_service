package faang.school.postservice.model.composite_id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class FollowerId {
    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "follower_id", nullable = false)
    private Long followerId;
}
