package faang.school.postservice.dto.comment;

import lombok.Data;

@Data
public class CommentDto {
    private Long id;
    private String content;
    private long authorId;
}
