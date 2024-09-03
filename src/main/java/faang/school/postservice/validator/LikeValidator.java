package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.DataNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LikeValidator {
    private final UserServiceClient userServiceClient;

    public void validateUserExistence(Long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException e) {
            throw new DataNotFoundException("Cant find user with id " + userId);
        }
    }

    public void validateLikeToPost(Post post, Long userId) {
        List<Like> likes = Optional.ofNullable(post.getLikes())
                .orElse(List.of(new Like()));
        boolean userAlreadyLikedPost = likes.stream()
                .anyMatch(existingLike -> userId.equals(existingLike.getUserId()));

        if (userAlreadyLikedPost) {
            throw new IllegalArgumentException("User has already liked this post.");
        }
    }

    public void validateLikeToComment(Comment comment, Long userId) {
        boolean userAlreadyLikedPost = comment.getLikes().stream()
                .anyMatch(existingLike -> existingLike.getUserId().equals(userId));

        if (userAlreadyLikedPost) {
            throw new IllegalArgumentException("User has already liked this comment.");
        }
    }
}
