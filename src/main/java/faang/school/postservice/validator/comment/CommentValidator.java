package faang.school.postservice.validator.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exeption.DataValidationException;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidator {
    private PostRepository postRepository;
    private UserServiceClient userServiceClient;

    public void idValidator(long id) {
        if (id < 1) {
            throw new DataValidationException("Id mast be more 1, your isn't");
        }
    }

    public void commentDtoValidator(CommentDto commentDto) {
        idValidator(commentDto.getId());
        postExistValidator(commentDto.getPostId());
        authorExistValidator(commentDto.getAuthorId());

        int lenComment = commentDto.getContent().length();
        if (lenComment > 4096 || lenComment == 0) {
            throw new DataValidationException("Length of comment is not correct");
        }
    }

    public void postExistValidator(long postId) {
        postRepository.findById(postId)
                .orElseThrow(() -> new DataValidationException("Post is not exist"));
    }

    public void authorExistValidator(long authorId) {
        if (userServiceClient.getUser(authorId) == null) {
            throw new DataValidationException("User is not exist");
        }
    }

}
