package faang.school.postservice.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class PostViewEvent {

    @JsonProperty("post_id") private Long postId;
    @JsonProperty("user_id") private Long userId;
    @JsonProperty("viewed_at") private Instant viewedAt;
}
