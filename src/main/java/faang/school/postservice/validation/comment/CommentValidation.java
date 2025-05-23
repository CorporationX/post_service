package faang.school.postservice.validation.comment;

import faang.school.postservice.exception.CommentNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;

import java.util.List;
import java.util.Objects;

public class CommentValidation {

    private static CommentRepository commentRepository;

    public static void existenceCheckTheComment(long commentId) {
        List<Comment> commentList = (List<Comment>) commentRepository.findAll();
        for (Comment comment : commentList) {
            if(commentId != comment.getId()) {
                throw new CommentNotFoundException(String.format("Comment by id %d not Found", commentId));
            }
        }
    }
}
