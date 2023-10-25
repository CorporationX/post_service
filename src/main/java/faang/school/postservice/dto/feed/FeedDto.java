package faang.school.postservice.dto.feed;

import faang.school.postservice.dto.post.PostCacheDto;
import faang.school.postservice.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedDto {
    private UserDto userDto;
    private LinkedHashSet<PostCacheDto> postCache;
}
