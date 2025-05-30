package faang.school.postservice.dto.post;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostPublishDto implements Serializable {
    private Long postId;
    private Long authorId;
    private List<Long> subscribersIds;
}
