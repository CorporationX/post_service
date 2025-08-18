package faang.school.postservice.config.properties;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "post.correction")
public record SpellCheckAsyncProperties(
        @Min(1) int batchSize,
        @Min(1) int coreSize,
        @Min(1) int maxSize,
        @Min(1) int queueCapacity
) {
    @AssertTrue(message = "maxSize must be >= coreSize")
    public boolean isPoolSizesValid() {
        return maxSize >= coreSize;
    }

    @AssertTrue(message = "queueCapacity must be >= batchSize")
    public boolean isQueueEnoughForBatch() {
        return queueCapacity >= batchSize;
    }
}
