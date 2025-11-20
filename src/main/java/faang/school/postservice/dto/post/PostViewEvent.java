package faang.school.postservice.dto.post;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record PostViewEvent(
        Long postId,
        Long authorId,
        Long viewerId,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime currentTime
){
}
