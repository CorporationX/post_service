package faang.school.postservice.event.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentKafkaEvent {

    private Long id;
    private Long postId;
    private Long authorId;
    private String content;
    private LocalDateTime updatedAt;
}