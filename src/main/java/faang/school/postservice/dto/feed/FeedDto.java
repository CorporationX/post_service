package faang.school.postservice.dto.feed;

import faang.school.postservice.dto.feed.redis.PostRedisDto;
import faang.school.postservice.dto.feed.redis.UserRedisDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedDto {
    private PostRedisDto postRedisDto;
    private UserRedisDto userRedisDto;
}
