package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.PostService;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidator {

    private final UserServiceClient userService;
    private final PostService postService;
    private final CommentRepository commentRepository;

    public Comment validCommentId(long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Comment with id:%d doesn't exist", commentId)));
    }

    public void validateData(CommentDto commentDto) {
        long postId = commentDto.getPostId();
        long authorId = commentDto.getAuthorId();

        try {
            userService.getUser(authorId);
        } catch (FeignException e) {
            throw new EntityNotFoundException(String.format("User with id:%d doesn't exist", authorId));
        }
        postService.getPost(postId);
    }
}
