package faang.school.postservice.dto.newsfeed.post;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostResponseDto {

    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private Long likes;
    private boolean published;
    private LocalDateTime publishedAt;
    private LocalDateTime scheduledAt;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean verified;
    private LocalDateTime verifiedAt;
}
