package faang.school.postservice.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostPublishedEventDto {
    private Long postId;
    private Long ownerId;
    private LinkedHashSet<Integer> authorSubscriberIds = new LinkedHashSet<>();
}
