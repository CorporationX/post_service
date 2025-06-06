package faang.school.postservice.config.post.media.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Getter
@Setter
@RefreshScope
@ConfigurationProperties(prefix = "post-media")
public class PostMediaProperties {

    private long maxPostMediaFilesAmount;

    private long maxImageSizeMb;
    private long maxAudioSizeMb;
    private long maxVideoSizeMb;

    //Image, Video, Audio
    private List<String> allowedContentTypePrefixes;

    public Map<String, Long> getTypeSpecificSizeLimits() {
        Map<String, Long> limits = new HashMap<>();
        limits.put("image", maxImageSizeMb);
        limits.put("video", maxVideoSizeMb);
        limits.put("audio", maxAudioSizeMb);
        return limits;
    }
}
