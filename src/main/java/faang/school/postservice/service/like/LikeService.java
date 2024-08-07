package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.comment.AuthorNotFoundException;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validation.comment.UserClientValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final UserClientValidation userClientValidation;

    public List<UserDto> getUsersByPostId(Long postId) {
        List<Like> likes = likeRepository.findByPostId(postId);
        if (likes.isEmpty()) {
            log.error("No likes found for postId: " + postId);
            throw new NoSuchElementException("No likes found for postId: " + postId);
        }

        List<Long> usersIds = likes.stream()
                .map(Like::getUserId)
                .toList();

        return getUserDtoByBatches(usersIds);
    }

    public List<UserDto> getUsersByCommentId(Long commentId) {
        List<Like> likes = likeRepository.findByCommentId(commentId);
        if (likes.isEmpty()) {
            log.error("No likes found for commentId: " + commentId);
            throw new NoSuchElementException("No likes found for commentId: " + commentId);
        }

        List<Long> usersIds = likes.stream()
                .map(Like::getUserId)
                .toList();
        return getUserDtoByBatches(usersIds);
    }

    private List<UserDto> getUserDtoByBatches(List<Long> usersIds) {
        List<UserDto> result = new ArrayList<>();
        int batchSize = 100;
        for (int i = 0; i < usersIds.size(); i += batchSize) {
            int end = Math.min(usersIds.size(), i + batchSize);
            List<Long> batch = usersIds.subList(i, end);
            for (Long userId : batch) {
                userClientValidation.checkUser(userId);
            }
            try {
                result.addAll(userServiceClient.getUsersByIds(batch));
            } catch (AuthorNotFoundException e) {
                log.error(e.getMessage());
                throw e;
            }
        }
        return result;
    }
}
