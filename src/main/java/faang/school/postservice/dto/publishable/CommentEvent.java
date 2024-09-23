package faang.school.postservice.dto.publishable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentEvent {
    private Long authorId;
    private Long postId;
}
