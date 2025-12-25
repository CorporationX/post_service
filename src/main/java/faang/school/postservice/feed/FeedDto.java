package faang.school.postservice.feed;

import lombok.Builder;

import java.util.List;

@Builder
public record FeedDto(
        List<PostFeedItemDto> posts,
        String nextCursor,
        boolean hasMore
) {
}
