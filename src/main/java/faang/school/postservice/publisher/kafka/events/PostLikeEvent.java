package faang.school.postservice.publisher.kafka.events;

import faang.school.postservice.dto.like.LikeAction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostLikeEvent {
    private Long postId;
    private LikeAction likeAction;
}
