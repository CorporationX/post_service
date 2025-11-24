package faang.school.postservice.dto.post;


import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.postservice.dto.user.UserDto;

import java.time.LocalDateTime;

public record PostViewEvent(
        Long postId,
        UserDto author,
        Long viewerId,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime currentTime
){
}
