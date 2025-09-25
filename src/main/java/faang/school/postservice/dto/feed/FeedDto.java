package faang.school.postservice.dto.feed;

import faang.school.postservice.dto.comment.CommentDto;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record FeedDto(
        long postId,
        String content,
        String author,
        long likes,
        long views,
        LocalDateTime createdAt,
        List<CommentDto> comments
) {}
