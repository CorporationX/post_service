package faang.school.postservice.dto.event;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class PostViewEvent {
    private long postId;
    private long authorId;
    private long userId;
    @Builder.Default
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime viewedAt = LocalDateTime.now();
}
