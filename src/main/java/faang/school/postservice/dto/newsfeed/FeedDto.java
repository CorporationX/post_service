package faang.school.postservice.dto.newsfeed;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedDto {
    private List<UserDto> userDtos;
    private List<PostDto> posts;
}
