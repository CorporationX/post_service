package faang.school.postservice.dto.feed_dto;

import faang.school.postservice.dto.UserDto;
import faang.school.postservice.dto.comment.CommentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostForFeedDto {
    private long id;
    private UserDto author;
    private String content;
    private LocalDateTime publishedAt;
    private AtomicLong likes;
    private TreeSet<CommentDto> comments;
}
