package faang.school.postservice.properties.kafka;

import lombok.Data;

@Data
public class Consumer {
    private boolean enableAutoCommit;
    private String isolationLevel;
}
