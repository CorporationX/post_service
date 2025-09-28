package faang.school.postservice.dto.event;

import faang.school.postservice.dto.post.PostDto;
import lombok.Builder;

import java.util.List;

@Builder
public record FeedHeatEvent(
        long userId,
        List<PostDto> posts
) {
}
