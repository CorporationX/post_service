package faang.school.postservice.message.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentEvent {
    private long receiverId;
    private String commentContent;
    private String commentAuthorUserName;
}
