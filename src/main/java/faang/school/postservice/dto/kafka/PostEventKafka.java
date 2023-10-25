package faang.school.postservice.dto.kafka;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PostEventKafka {
    private long id;
    private String content;
    private Long authorId;
    private List<Long> followersIds;
}
