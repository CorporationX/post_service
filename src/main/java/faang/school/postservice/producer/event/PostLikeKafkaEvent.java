package faang.school.postservice.producer.event;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record PostLikeKafkaEvent(
        long postId,
        long userId,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp
) {
}
