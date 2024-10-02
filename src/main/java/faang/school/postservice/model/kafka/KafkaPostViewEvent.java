package faang.school.postservice.model.kafka;

import lombok.Data;

import java.io.Serializable;

@Data
public class KafkaPostViewEvent implements Serializable {

    private long viewedPostId;

}
