package faang.school.postservice.aop.comment;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;

public record CommentCreatedEvent(
        Comment comment,
        Post post,
        UserDto user
) { }
