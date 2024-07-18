package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.repository.CommentRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidation {
    private final UserServiceClient userServiceClient;
    private final CommentRepository commentRepository;

    public void authorExistenceValidation(Long userId) {
        try {
            UserDto user = userServiceClient.getUser(userId);
        } catch (FeignException e) {
            throw new EntityNotFoundException("User with id " + userId + " is not found");
        }
    }

    public void validateCommentExistence(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new DataValidationException("No comment with id" + commentId + " found");
        }
    }

}
