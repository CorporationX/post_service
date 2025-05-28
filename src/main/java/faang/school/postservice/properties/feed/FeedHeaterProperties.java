package faang.school.postservice.properties.feed;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "feed.heater")
public class FeedHeaterProperties {

    @Min(1)
    private Integer pageSize;
}
