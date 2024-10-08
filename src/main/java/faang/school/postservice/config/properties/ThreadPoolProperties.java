package faang.school.postservice.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("executor")
public class ThreadPoolProperties {

    private int capacity;
    private int corePoolSize;
    private int maximumPoolSize;
    private long keepAliveTime;

}
