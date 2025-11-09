package faang.school.postservice.config.resource;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "app.resources")
public class ResourceProperties {
    @Min(1024)
    private long maxFileSize;

    @Min(1)
    @Max(50)
    private int maxFilesPerPost;

    @Min(100)
    private int imageMaxWidth;

    @Min(100)
    private int imageMaxHeight;

    @NotEmpty
    private List<String> allowedContentTypes;
}
