package faang.school.postservice.dto.like;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@RequiredArgsConstructor
public class LikeForCommentDto {
    private long userId;
    private long commentId;

    private LocalDateTime createdAt;
}
