package faang.school.postservice.dto.feed;


import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedDto {
    private List<PostDto> posts;
    private List<UserDto> postAuthors;
}
