package faang.school.postservice.dto.event;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentEventKafka {
    private long commentId;
    private long postId;
    private String content;
    private UserDto userDto;

    public CommentEventKafka(Comment comment, UserDto userDto) {
        this.commentId = comment.getId();
        this.postId = comment.getPost().getId();
        this.content = comment.getContent();
        this.userDto = userDto;
    }
}
