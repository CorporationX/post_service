package faang.school.postservice.dto.kafka;

import faang.school.postservice.dto.redis.TimedPostId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KafkaPostDto {
    private Long userId;
    private TimedPostId post;
}
