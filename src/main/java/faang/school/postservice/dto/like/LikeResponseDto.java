package faang.school.postservice.dto.like;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record LikeResponseDto(

        @JsonProperty("id")
        long id,

        @JsonProperty("userId")
        long userId,

        @JsonProperty("commentId")
        Long commentId,

        @JsonProperty("postId")
        Long postId,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonProperty("createdAt")
        LocalDateTime createdAt
) {
}
