package faang.school.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostEventDto {
    private Long postId;
    private String content;
    private Long authorId;
    private String authorName;
    private Long timeStamp;
}
