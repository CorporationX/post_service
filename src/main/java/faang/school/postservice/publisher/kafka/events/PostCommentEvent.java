package faang.school.postservice.publisher.kafka.events;

import faang.school.postservice.dto.comment.CommentNewsFeedDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentEvent {
    private CommentNewsFeedDto commentNewsFeedDto;
}
