package faang.school.postservice.config.processor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResourceProcessorConfig {
    @Value("${resource.max-width-landscape}")
    private int MAX_WIDTH_LANDSCAPE;
    @Value("${resource.max-height-landscape}")
    private int MAX_HEIGHT_LANDSCAPE;
    @Value("${resource.max-dimension-square}")
    private int MAX_DIMENSION_SQUARE;

    @Bean
    public int maxWidthLandscape() {
        return MAX_WIDTH_LANDSCAPE;
    }

    @Bean
    public int maxHeightLandscape() {
        return MAX_HEIGHT_LANDSCAPE;
    }

    @Bean
    public int maxDimensionSquare() {
        return MAX_DIMENSION_SQUARE;
    }
}
