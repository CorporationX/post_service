package faang.school.postservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikeCount {
    private Long postId;
    private Long count;
}
