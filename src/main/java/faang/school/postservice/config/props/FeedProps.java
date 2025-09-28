package faang.school.postservice.config.props;

import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "feed")
public record FeedProps(
        @NonNull String key,
        @NonNull String keySet,
        @DefaultValue("86400") long ttl,
        @DefaultValue("500") int maxStored,
        @NonNull Post post,
        @NonNull User user,
        @NonNull Comment comment
) {
    public record Post(
            @NonNull String key,
            @NonNull String keySet,
            @DefaultValue("86400") long ttl
    ) {}

    public record User(
            @DefaultValue("86400") long ttl,
            @DefaultValue("1000") int batch
    ) {}

    public record Comment(
            @DefaultValue("3") int limit,
            @DefaultValue("86400") long ttl
    ) {}
}
