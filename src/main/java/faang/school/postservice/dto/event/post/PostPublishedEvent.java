package faang.school.postservice.dto.event.post;

import faang.school.postservice.dto.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PostPublishedEvent extends Event {
    private Long postId;
    private List<Long> followerIds;
}
