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

    public FeedDto setAuthorInfo(String authorInfo) {
        this.authorInfo = authorInfo;
        return this;
    }

    public FeedDto setCommentInfo(Set<String> commentInfo) {
        this.commentInfo = commentInfo;
        return this;
    }
}
