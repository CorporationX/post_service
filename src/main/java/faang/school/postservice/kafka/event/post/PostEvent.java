package faang.school.postservice.kafka.event.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import faang.school.postservice.kafka.event.KafkaEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Jacksonized
@AllArgsConstructor
public class PostEvent implements KafkaEvent {

    private Long postId;
    private Long authorId;
    private String content;
    private List<Long> followersIds;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime publishedAt;
}
