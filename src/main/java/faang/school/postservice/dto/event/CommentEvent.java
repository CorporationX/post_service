package faang.school.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentEvent extends Event {
    private Long id;
    private String content;
    private Long authorId;
    private Long postId;
}
