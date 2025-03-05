package faang.school.postservice.dto.feed;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FeedResponse {
    private List<PostDTO> posts;
    private boolean hasMore;
    private Long lastPostId;
}