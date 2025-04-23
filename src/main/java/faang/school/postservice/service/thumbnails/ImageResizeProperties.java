package faang.school.postservice.service.thumbnails;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "image-s3")
public class ImageResizeProperties {

    private List<Integer> resizeSizes;
}
