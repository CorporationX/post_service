package faang.school.postservice.dto.kafka;

import faang.school.postservice.dto.PostPair;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class KafkaPostEvent {

    private PostPair postPair;
    private List<Long> followersIds;
}
