package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private static final int USER_BATCH_SIZE = 100;

    @Transactional(readOnly = true)
    public List<UserDto> getUsersThatLikedPost(Long postId) {
        List<Long> userIdsThatLikedPost = getLikesFromPost(postId);
        return getUsersFromUserService(userIdsThatLikedPost);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsersThatLikedComment(Long commentId) {
        List<Long> userIdsThatLikedComment = getLikesFromComment(commentId);
        return getUsersFromUserService(userIdsThatLikedComment);
    }

    @Transactional
    public UserDto deleteLikeFromPost(Long postId, Long userId) {
        Like like = likeRepository.findByPostIdAndUserId(postId, userId).orElseThrow(() -> new EntityNotFoundException("Like was not found"));
        likeRepository.delete(like);
        return userServiceClient.getUser(userId);
    }

    @Transactional
    public UserDto deleteLikeFromComment(Long commentId, Long userId) {
        Like like = likeRepository.findByCommentIdAndUserId(commentId, userId).orElseThrow(() -> new EntityNotFoundException("Like was not found"));
        likeRepository.delete(like);
        return userServiceClient.getUser(userId);
    }

    private List<Long> getLikesFromPost(Long postId) {
        return likeRepository.findByPostId(postId).stream().map(Like::getUserId).toList();
    }

    private List<Long> getLikesFromComment(Long commentId) {
        return likeRepository.findByCommentId(commentId).stream().map(Like::getUserId).toList();
    }

    private List<UserDto> getUsersFromUserService(List<Long> userIds) {
        List<UserDto> users = new ArrayList<>();
        for (int i = 0; i < userIds.size(); i += USER_BATCH_SIZE) {
            int bound = Math.min(i + USER_BATCH_SIZE, userIds.size());
            List<UserDto> batchOfUsers = userServiceClient.getUsersByIds(userIds.subList(i, bound));
            users.addAll(batchOfUsers);
        }
        return users;
    }
}
