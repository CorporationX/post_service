package faang.school.postservice.config;


import faang.school.postservice.service.resource.MimeConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MimeConverterConfig {

    @Value("${resources.image.allowed-types}")
    private List<String> allowedImageTypes;

    @Value("${resources.audio.allowed-types}")
    private List<String> allowedAudioTypes;

    @Value("${resources.video.allowed-types}")
    private List<String> allowedVideoTypes;

    @Bean
    public MimeConverter mimeConverter() {
        return new MimeConverter(allowedImageTypes, allowedAudioTypes, allowedVideoTypes);
    }
}
