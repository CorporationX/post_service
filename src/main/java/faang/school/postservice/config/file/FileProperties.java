package faang.school.postservice.config.file;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "file")
@Component
@Data
public class FileProperties {
    private long maxSize;
}
