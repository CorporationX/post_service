package faang.school.postservice.dto.posts;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostSaveDto {
    private Long authorId;
    private Long projectId;
    private String content;
}
