package faang.school.postservice.dto.feed;

import faang.school.postservice.dto.post.PostCacheDto;
import faang.school.postservice.model.RedisUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedDto {
    private RedisUser redisUser;
    private LinkedHashSet<PostCacheDto> posts;
}
