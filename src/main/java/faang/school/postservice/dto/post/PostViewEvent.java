package faang.school.postservice.dto.post;


import java.time.LocalDateTime;

public record PostViewEvent(
        Long postId,
        Long authorId,
        Long viewerId,
        LocalDateTime currentTime
){
}
