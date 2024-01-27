package faang.school.postservice.dto.post;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PostDto {
    private String content;
    private Long authorId;
    private Long projectId;
//    private List<Long> likes;
}
