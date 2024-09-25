package faang.school.postservice.dto.feed;

import faang.school.postservice.dto.post.PostFeedDto;
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

    private Long userId;

    private List<PostFeedDto> posts;
}