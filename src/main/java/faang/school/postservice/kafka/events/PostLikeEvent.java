package faang.school.postservice.kafka.events;

import faang.school.postservice.dto.comment.CommentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostLikeEvent {
    private Long id;
    private String content;
    private Long authorId;
    private Integer likes;
    private List<CommentDto> comments;
}