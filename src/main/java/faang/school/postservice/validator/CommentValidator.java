package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.CommentValidationException;
import faang.school.postservice.model.Comment;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidator {
    private final UserServiceClient userServiceClient;
    public void validate(Comment comment) {
        try {
            userServiceClient.getUser(comment.getAuthorId());
        } catch (FeignException.FeignClientException exception) {
            throw new CommentValidationException();
        }

    }
}
