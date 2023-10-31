package faang.school.postservice.messaging.kafka.events;

import faang.school.postservice.model.Resource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostEvent {

    private long authorId;

    private List<Long> followersIds;
    private LocalDateTime publishDate;

    private String content;
    private List<Resource> resources;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
}
