package faang.school.postservice.dto.feed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedDto {
    private String postInfo;
    private String authorInfo;
    private Set<String> commentInfo;
    private Long likeInfo;
}
