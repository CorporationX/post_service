package faang.school.postservice.dto.feed;

public record FeedWarmupBatchEvent(
        int page,
        int size
) {}
