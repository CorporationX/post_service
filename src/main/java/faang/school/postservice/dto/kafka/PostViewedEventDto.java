package faang.school.postservice.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostViewedEventDto {
    private Long postId;
    private Long ownerId;
    private Long viewerId;
}
