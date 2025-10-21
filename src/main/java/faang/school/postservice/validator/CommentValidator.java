package faang.school.postservice.validator;

import faang.school.postservice.dto.comment.Request.RequestCommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class CommentValidator {

    public void validateAuthorComment(RequestCommentDto commentDto, long contextIdUser) {
        if (!Objects.equals(contextIdUser, commentDto.getAuthorId())) {
            log.warn("The user is trying to change someone else`s data ,{} - the user, {} - the Original user",
                contextIdUser, commentDto.getAuthorId());
            throw new IllegalArgumentException("You cannot change someone else's data");
        }
    }

    public void validateCommentToPost(Comment existingComment, Post postById) {
        if (!existingComment.getPost().getId().equals(postById.getId())) {
            throw new IllegalArgumentException("Comment does not belong to the specified post");
        }
    }
}