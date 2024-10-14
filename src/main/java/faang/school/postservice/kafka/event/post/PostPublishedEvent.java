package faang.school.postservice.kafka.event.post;

import faang.school.postservice.kafka.event.Event;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PostPublishedEvent extends Event {
    private Long postId;
    private List<Long> followerIds;
}
