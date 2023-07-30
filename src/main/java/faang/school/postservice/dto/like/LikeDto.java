package faang.school.postservice.dto.like;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class LikeDto {
    private long id;
    private Long userId;
    private Long comment;
    private Long post;
    private LocalDateTime createdAt;
}
