package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidator {
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public void validationCreateComment(long postId, CommentDto commentDto) {

        if (commentDto.getContent() == null) {
            throw new DataValidationException("The content is null");
        }

        if (commentDto.getContent().length() > 4096 && commentDto.getContent().isBlank()) {
            throw new DataValidationException("The content contains more than 4096 characters or the content is empty");
        }

        if (commentDto.getCreatedAt() == null) {
            throw new DataValidationException("CreatedAt is null");
        }

        userServiceClient.getUser(commentDto.getAuthorId());

        postRepository.findById(postId).orElseThrow(() -> new DataValidationException("Post is null"));
    }

    public void validationUpdateComment(long postId, CommentDto commentDto) {
        if (commentDto.getContent() == null) {
            throw new DataValidationException("The content is null");
        }

        if (commentDto.getId() == null) {
            throw new DataValidationException(" id's comment is null");
        }

        postRepository.findById(commentDto.getAuthorId()).orElseThrow(() ->
                new DataValidationException("AuthorId is null"));

        Comment comment = commentRepository.findById(commentDto.getId()).orElseThrow(() ->
                new DataValidationException("Comment is null"));

        if (comment.getAuthorId() != commentDto.getAuthorId() &&
                comment.getUpdatedAt() != commentDto.getCreatedAt() &&
                comment.getUpdatedAt() != commentDto.getUpdatedAt()) {
            throw new DataValidationException("immutable data has been changed");
        }
    }
}
