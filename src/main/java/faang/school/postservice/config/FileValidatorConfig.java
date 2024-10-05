package faang.school.postservice.config;

import faang.school.postservice.service.resource.validator.AudioFileValidator;
import faang.school.postservice.service.resource.validator.ImageFileValidator;
import faang.school.postservice.service.resource.validator.VideoFileValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileValidatorConfig {

    @Value("${resources.image.max-size}")
    private long maxImageSize;
    @Value("${resources.audio.max-size}")
    private long maxAudioSize;
    @Value("${resources.video.max-size}")
    private long maxVideoSize;

    @Value("${resources.image.max-in-post}")
    private int maxImageInPost;
    @Value("${resources.audio.max-in-post}")
    private int maxAudioInPost;
    @Value("${resources.video.max-in-post}")
    private int maxVideoInPost;

    @Bean
    public ImageFileValidator imageFileValidator() {
        return new ImageFileValidator(maxImageSize, maxImageInPost);
    }

    @Bean
    public AudioFileValidator audioFileValidator() {
        return new AudioFileValidator(maxAudioSize, maxAudioInPost);
    }

    @Bean
    public VideoFileValidator videoFileValidator() {
        return new VideoFileValidator(maxVideoSize, maxVideoInPost);
    }
}
