package faang.school.postservice.dto.kafka_events;

import faang.school.postservice.model.Like;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikeKafkaEvent {
    private Long id;
    private Long authorId;
    private Long postId;

    public LikeKafkaEvent(Like like) {
        this.id = like.getId();
        this.authorId = like.getUserId();
        this.postId = like.getPost().getId();
    }
}
