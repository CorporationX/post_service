package faang.school.postservice.config.validator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PostServiceValidatorConfiguration {
    @Value("${post.validation-param.obsolescence-period-date-publication}")
    private long OBSOLESCENCE_PERIOD_DATE_PUBLICATION;

    @Value("${post.validation-param.max-post-resource}")
    private long MAX_POST_RESOURCE;

    @Bean
    public long OBSOLESCENCE_PERIOD_DATE_PUBLICATION() {
        return OBSOLESCENCE_PERIOD_DATE_PUBLICATION;
    }

    @Bean
    public long MAX_POST_RESOURCE() {
        return MAX_POST_RESOURCE;
    }
}
