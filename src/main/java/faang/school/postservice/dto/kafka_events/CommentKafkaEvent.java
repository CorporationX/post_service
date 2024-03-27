package faang.school.postservice.dto.kafka_events;

import faang.school.postservice.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentKafkaEvent {
    private long id;
    private String content;
    private long authorId;
    private long postId;
    private LocalDateTime createdAt;

    public CommentKafkaEvent(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.authorId = comment.getAuthorId();
        this.postId = comment.getPost().getId();
        this.createdAt = comment.getCreatedAt();
    }
}
