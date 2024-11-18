package faang.school.postservice.config.kafka;

import lombok.Data;

@Data
public class Consumer {
    private boolean enableAutoCommit;
    private String isolationLevel;
}
