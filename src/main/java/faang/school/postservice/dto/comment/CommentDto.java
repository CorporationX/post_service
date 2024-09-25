package faang.school.postservice.dto.comment;

import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class CommentDto {
    private Long id;
    private String content;
    private long authorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
