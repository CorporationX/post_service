package faang.school.postservice.dto.post;

import faang.school.postservice.dto.user.UserDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostForFeedDto {
    private UserDto postAuthor;
    private PostDto post;
}
