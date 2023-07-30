package faang.school.postservice.validator.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exeption.DataValidationException;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidator {
    private PostService postService;
    private UserServiceClient userServiceClient;

    public void validatorId(long id) {
        if (id < 1) {
            throw new DataValidationException("Id mast be more 1, your isn't");
        }
    }

    public void ValidatorCommentDto(CommentDto commentDto) {
        validatorId(commentDto.getId());
        validatorPostExist(commentDto.getPostId());
        validatorAuthorExist(commentDto.getAuthorId());

        int lenComment = commentDto.getContent().length();
        if (lenComment >= 4096 || lenComment == 0) {
            throw new DataValidationException("Length of comment is not correct");
        }
    }

    public void validatorPostExist(long postId) {
        validatorId(postId);
        postService.getPostById(postId);
    }

    public void validatorAuthorExist(long authorId) {
        if (userServiceClient.getUser(authorId) == null) {
            throw new DataValidationException("User is not exist");
        }
    }
}
