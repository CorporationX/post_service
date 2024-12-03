package faang.school.postservice.event.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreatedEvent {

    @JsonProperty("comment_id")
    private Long commentId;
    @JsonProperty("author_id")
    private Long authorId;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    @JsonProperty("content")
    private String content;
    @JsonProperty("post_id")
    private Long postId;
}

