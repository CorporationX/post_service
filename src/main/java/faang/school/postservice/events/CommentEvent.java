package faang.school.postservice.events;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentEvent {
    private Long commentId;
    private Long postId;
    private Long authorId;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public CommentEvent(Long commentId, Long postId, Long authorId) {
        this.commentId = commentId;
        this.postId = postId;
        this.authorId = authorId;
    }
}
