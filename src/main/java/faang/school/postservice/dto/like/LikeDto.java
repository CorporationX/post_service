package faang.school.postservice.dto.like;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeDto {
    private long id;
    private Long userId;
    private Comment comment;
    private Post post;
}
