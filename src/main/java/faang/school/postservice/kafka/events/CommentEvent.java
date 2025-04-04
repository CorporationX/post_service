package faang.school.postservice.kafka.events;

import faang.school.postservice.dto.comment.CommentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentEvent {
    private CommentDto commentDto;
    private Long authorId;
    private Long postId;
    private String content;
}