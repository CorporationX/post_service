package faang.school.postservice.dto.hashtag;

import lombok.Data;

@Data
public class PostResponseDto {
    private long id;
    private String content;
    private Long authorId;
    private Long projectId;
}
