package faang.school.postservice.service.post.validator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidatorConfiguration {
    @Value("${validation-param.obsolescence-period-date-publication}")
    private long obsolescencePeriodDatePublication;

    @Bean
    public long obsolescencePeriodDatePublication() {
        return obsolescencePeriodDatePublication;
    }
}
