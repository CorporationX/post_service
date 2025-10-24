package faang.school.postservice.validator;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CommentValidator {

    public void validateCommentToPost(Comment existingComment, Post postById) {
        if (!existingComment.getPost().getId().equals(postById.getId())) {
            throw new IllegalArgumentException("Comment does not belong to the specified post");
        }
    }
}