package faang.school.postservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostLikeDto {

    private Long id;
    private Long userId;
    private Long postId;
    private LocalDateTime createdAt;
}

