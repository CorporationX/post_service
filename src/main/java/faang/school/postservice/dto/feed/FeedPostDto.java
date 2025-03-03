package faang.school.postservice.dto.feed;

import faang.school.postservice.model.feed.PostCache;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedPostDto {

    private String content;
    private Long authorId;
    private String authorUsername;
    private Long likes;
    private Long comments;
}
