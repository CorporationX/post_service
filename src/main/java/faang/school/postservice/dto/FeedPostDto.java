package faang.school.postservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedPostDto {
    private Long postId;
    private Integer pageAmount;

    public boolean containsPostId() {
        return postId != null;
    }

    public boolean containsPageAmount() {
        return pageAmount != null;
    }

}
