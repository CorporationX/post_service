package faang.school.postservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentCount {
    private Long postId;
    private Long count;
}
