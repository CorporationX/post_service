package faang.school.postservice.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class KafkaHeatFeedSizeDto {
    private int pageNumber;
    private int pageSize;
}
