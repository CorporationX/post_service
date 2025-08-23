package faang.school.postservice.dto.feed;

import faang.school.postservice.dto.redis.RedisPostDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FeedDto {
    List<RedisPostDto> posts;
}
