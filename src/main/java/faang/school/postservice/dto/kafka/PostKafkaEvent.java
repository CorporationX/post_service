package faang.school.postservice.dto.kafka;

import faang.school.postservice.model.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostKafkaEvent {
    private long postId;
    private long authorId;
    private List<Long> subscriberIds;

    public PostKafkaEvent(Post post, List<Long> subscriberIds) {
        this.postId = post.getId();
        this.authorId = post.getAuthorId();
        this.subscriberIds = subscriberIds;
    }
}