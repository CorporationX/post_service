package faang.school.postservice.aop.comment;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommentCreatedEvent {
    private final Comment comment;
    private final Post post;
    private final UserDto user;
}
