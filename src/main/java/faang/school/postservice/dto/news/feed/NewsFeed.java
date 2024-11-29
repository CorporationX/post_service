package faang.school.postservice.dto.news.feed;

import faang.school.postservice.dto.post.PostDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsFeed {
    private List<PostDto> posts;
}
