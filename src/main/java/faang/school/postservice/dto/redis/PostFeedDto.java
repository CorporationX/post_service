package faang.school.postservice.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostFeedDto implements Comparable<PostFeedDto> {
    private long postId;
    private LocalDateTime publishedAt;
    @Override
    public int compareTo(PostFeedDto postFeedDto) {
        return publishedAt.compareTo(postFeedDto.publishedAt);
    }
}
