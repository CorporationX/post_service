package faang.school.postservice.validator.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommentValidator {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public void findPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Post not found!"));
    }

    public void findCommentById(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new RuntimeException("Comment not found!"));
    }

    public void checkUserRightsToChangeComment(Comment comment, CommentDto commentDto) {
        if (comment.getAuthorId() != commentDto.getAuthorId()) {
            throw new RuntimeException("You don't have enough rights!");
        }
    }
}
