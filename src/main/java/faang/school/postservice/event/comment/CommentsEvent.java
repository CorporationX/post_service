package faang.school.postservice.event.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CommentsEvent {
    private final UUID uuid = UUID.randomUUID();
    private long commentId;
    private long authorId;
    private long postId;
    private String content;
}
