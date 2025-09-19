package faang.school.postservice.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class PostPublishEvent {
    Long authorId;
    Long postId;
    List<Long> followersId;
}
