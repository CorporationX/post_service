package faang.school.postservice.dto.post;

import faang.school.postservice.model.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class GetPostsDto {
    private Long authorId;
    private Long projectId;
    private PostStatus status;
}
