package faang.school.postservice.dto;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import lombok.Data;

@Data
public class LikeDto {
    private final Long id;
    private final Long userId;
    private final Comment comment;
    private final Post post;
}
