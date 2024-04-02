package faang.school.postservice.dto.kafka;

import faang.school.postservice.model.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KafkaPostEvent {

    private Post post;
    private List<Long> subscriberIds;

}
