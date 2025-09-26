package faang.school.postservice.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostPublishEvent {
    private Long authorId;
    private Long postId;
    private List<Long> followersId;
}
