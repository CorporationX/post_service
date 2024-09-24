package faang.school.postservice.dto.post;

import faang.school.postservice.model.redis.CommentRedis;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.TreeSet;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostFeedDto {

    private Long id;
    private String content;
    private Long authorId;
    private String authorName;
    private Long projectId;
    private Long likes;
    private Long views;
    private TreeSet<CommentRedis> commentRedis;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}