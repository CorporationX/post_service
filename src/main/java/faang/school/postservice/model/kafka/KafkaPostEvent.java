package faang.school.postservice.model.kafka;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class KafkaPostEvent implements Serializable {

    private long postAuthorId;
    private List<Long> authorFollowerIds;
    private String content;
}
