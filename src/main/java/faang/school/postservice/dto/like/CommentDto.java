package faang.school.postservice.dto.like;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private String content;
    private Long authorId;
    private List<Long> likesIds;
    private Long postId;
    private Integer likeCount;
}
