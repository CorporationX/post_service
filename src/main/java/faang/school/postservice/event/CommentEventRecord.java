package faang.school.postservice.event;

import faang.school.postservice.dto.comment.CommentDto;
import lombok.Builder;

@Builder
public record CommentEventRecord(CommentDto commentDto, Long authorId, Long postId, String content) {}