package faang.school.postservice.dto.kafka;

import faang.school.postservice.dto.redis.RedisCommentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KafkaCommentEvent {

    private Long postId;
    private RedisCommentDto commentDto;
}
