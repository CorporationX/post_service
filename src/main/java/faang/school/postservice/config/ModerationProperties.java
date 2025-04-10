package faang.school.postservice.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "moderation")
@Validated
public class ModerationProperties {

    @NotNull
    @Min(1)
    private Integer batchSize;

    @NotNull
    private Resource dictionary;

    @NotNull
    @Min(1)
    private Integer corePoolSize;

    @NotNull
    @Min(1)
    private Integer maxPoolSize;

    @NotNull
    @Min(0)
    private Integer queueCapacity;

    public @NotNull @Min(1) Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(@NotNull @Min(1) Integer batchSize) {
        this.batchSize = batchSize;
    }

    public @NotNull Resource getDictionary() {
        return dictionary;
    }

    public void setDictionary(@NotNull Resource dictionary) {
        this.dictionary = dictionary;
    }

    public @NotNull @Min(1) Integer getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(@NotNull @Min(1) Integer corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public @NotNull @Min(1) Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(@NotNull @Min(1) Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public @NotNull @Min(0) Integer getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(@NotNull @Min(0) Integer queueCapacity) {
        this.queueCapacity = queueCapacity;
    }
}
