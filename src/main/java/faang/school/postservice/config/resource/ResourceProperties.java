package faang.school.postservice.config.resource;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "resource")
public class ResourceProperties {

    private int maxFilesPerPost;

    private MediaConfig image;
    private MediaConfig video;
    private MediaConfig audio;

    @Data
    public static class MediaConfig {
        private int maxFileSizeMb;
        private List<String> allowedTypes;
        private Resize resize;
    }

    @Data
    public static class Resize {
        private Dimension square;
        private Dimension horizontal;
    }

    @Data
    public static class Dimension {
        private int width;
        private int height;
    }
}

