package faang.school.postservice.config.image;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "image.processing")
@Getter
@Setter
public class ImageProcessingProperties {
    private int maxFileSizeMb;
    private int maxCountPerPost;
    private ImageResizeProperties resize;
    private List<String> allowedContentTypes;
}
