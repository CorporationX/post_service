package faang.school.postservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentLikeDto {

    private Long id;
    private Long userId;
    private Long commentId;
    private LocalDateTime createdAt;
}

