package faang.school.postservice.dto.posts;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PostSaveDto {
    private Long authorId;
    private Long projectId;
    private String content;
}
