package faang.school.postservice.validation;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.PostService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class CommentValidator {
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UserServiceClient userServiceClient;

    public void validateAuthorExist(CommentDto commentDto){
        try {
            userServiceClient.getUser(commentDto.getAuthorId());
        } catch (FeignException e) {
            throw new DataValidationException("The author of the comment was not found in the system");
        }
    }

    public void validateCommentBeforeCreate(CommentDto commentDto){
        Long post = commentDto.getPostId();
        if (post == null) {
            throw new DataValidationException("Post Id is not specified");
        }
    }

    public void validateCommentBeforeUpdate(Long commentId, CommentDto commentDto){
        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        if (existingComment.getAuthorId() != commentDto.getAuthorId()) {
            throw new DataValidationException("Changing the author of the comment is prohibited");
        }

        if (existingComment.getPost().getId() != commentDto.getPostId()) {
            throw new DataValidationException("Changing the post id is prohibited");
        }
    }

    public void validateCommentBeforeGetCommentsByPostId(Long postId){
        try {
            Post post = postService.getPostIfExist(postId);
        }catch (EntityNotFoundException e){
            throw new EntityNotFoundException("Can't find comments because post does not exist");
        }
    }
}
