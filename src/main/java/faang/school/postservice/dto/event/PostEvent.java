package faang.school.postservice.dto.event;

import lombok.Data;

import java.util.List;

@Data
public class PostEvent {
    private Long postId;
    private Long authorId;
    private String content;
    private List<Long> followersIds;
}
