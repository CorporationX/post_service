package faang.school.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostViewKafkaEvent {
    private Long postId;
}
