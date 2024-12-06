package faang.school.postservice.properties.kafka;

import lombok.Data;

@Data
public class Producer {
    private String acks;
    private String transactionalId;
    private boolean enableIdempotence;
    private int retries;
    private int maxInFlightRequestsPerConnection;
}
