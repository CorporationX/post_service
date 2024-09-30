package faang.school.postservice.dto.feed;

import faang.school.postservice.dto.redis.PostDtoRedis;
import faang.school.postservice.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostFeedDto {
//    private long id;
    private UserDto userDto;
    private PostDtoRedis postDtoRedis;
}
