package faang.school.postservice.dto.post;

import faang.school.postservice.dto.comment.CommentDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCacheDto {
    private long postId;
    private LinkedHashSet<CommentDto> comments;
    private long likeCounter;
    private long views;
    private LocalDateTime createdAt;
}
