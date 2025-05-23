package faang.school.postservice.dto.like;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private List<Long> likesIds;
    private Integer likeCount;
}
