package faang.school.postservice.dto.post;

import faang.school.postservice.dto.comment.CommentCacheDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCacheDto {
    private Long id;
    private String content;
    private Long authorId;
    private int likeCount;
    private int viewCount;
    private List<CommentCacheDto> comments;
}

