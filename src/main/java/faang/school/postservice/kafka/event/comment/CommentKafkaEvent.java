package faang.school.postservice.kafka.event.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import faang.school.postservice.kafka.event.KafkaEvent;
import faang.school.postservice.kafka.event.State;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Data
@Builder
@Jacksonized
@AllArgsConstructor
public class CommentKafkaEvent implements KafkaEvent {

    private Long id;
    private String content;
    private Long userId;
    private long likesCount;
    private Long postId;
    private State state;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdAt;
}
