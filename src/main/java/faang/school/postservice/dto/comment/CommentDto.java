package faang.school.postservice.dto.comment;

import faang.school.postservice.model.Like;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String content;
    private long authorId;
//    private List<Long> likes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
