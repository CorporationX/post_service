package faang.school.postservice.dto.feed;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostFeedHeatDto {

    private Long userId;
    private List<PostTimeMarkDto> postTimeMarkDtos;
}
