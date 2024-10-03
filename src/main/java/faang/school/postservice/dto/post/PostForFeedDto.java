package faang.school.postservice.dto.post;

import faang.school.postservice.dto.comment.CommentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostForFeedDto {
    private PostDto postDto;
    private List<CommentDto> commentDto;
    private Long likes;
    private Long views;
}
