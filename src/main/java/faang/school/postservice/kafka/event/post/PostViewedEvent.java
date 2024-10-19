package faang.school.postservice.kafka.event.post;

import faang.school.postservice.kafka.event.Event;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public class PostViewedEvent extends Event {
    private Long postId;
    private Long currentViews;
}
