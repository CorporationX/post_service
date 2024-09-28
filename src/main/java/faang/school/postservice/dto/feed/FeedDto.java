package faang.school.postservice.dto.feed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedDto {
    private String postInfo;
    private String authorInfo;
    private Set<String> commentInfo;
    private Long likeInfo;
}
