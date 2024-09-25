package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.util.ExceptionThrowingValidator;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class LikeService {

    private static final int BATCH_SIZE = 100;

    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final ExceptionThrowingValidator validator;

    public List<UserDto> getAllUsersLikedPost(long postId) {
        List<Like> likesByPostId = likeRepository.findByPostId(postId);
        List<Long> userIds = likesByPostId.stream().map(Like::getUserId).toList();
        List<UserDto> usersByButches = getUsersByButches(userIds);
        usersByButches.forEach(validator::validate);
        return usersByButches;
    }

    public List<UserDto> getAllUsersLikedComment(long commentId) {
        List<Like> likesByCommentId = likeRepository.findByCommentId(commentId);
        List<Long> userIds = likesByCommentId.stream().map(Like::getUserId).toList();
        List<UserDto> usersByButches = getUsersByButches(userIds);
        usersByButches.forEach(validator::validate);
        return usersByButches;
    }

    private List<UserDto> getUsersByButches(List<Long> userIds) {

        List<UserDto> users = new ArrayList<>();

        for (int indexFromInclusive = 0; indexFromInclusive < userIds.size(); indexFromInclusive += BATCH_SIZE) {

            int indexToExclusive = Math.min(indexFromInclusive + BATCH_SIZE, userIds.size());
            List<Long> batchIds = userIds.subList(indexFromInclusive, indexToExclusive);

            try {
                users.addAll(userServiceClient.getUsersByIds(batchIds));
            } catch (FeignException ex1) {
                log.info("Exception when requesting users from userServiceClient by batch, ids {}", userIds, ex1);
                users.addAll(getUserById(batchIds));
            }
        }

        return users;
    }

    private List<UserDto> getUserById(List<Long> batchIds) {

        List<UserDto> users = new ArrayList<>();

        for (Long userId : batchIds) {
            try {
                UserDto user = userServiceClient.getUser(userId);
                users.add(user);
            } catch (FeignException ex2) {
                log.info("Exception when requesting user by id from userServiceClient, id {}", userId, ex2);
            }
        }

        return users;
    }
}
