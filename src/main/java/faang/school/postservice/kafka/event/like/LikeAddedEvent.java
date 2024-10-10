package faang.school.postservice.kafka.event.like;

import faang.school.postservice.kafka.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LikeAddedEvent extends Event {
    private Long postId;
}
