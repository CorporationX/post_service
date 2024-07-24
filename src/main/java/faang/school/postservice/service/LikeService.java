package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

@Service
@RequiredArgsConstructor
@Setter
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;

    @Value("$(like-service.batch-size)")
    private int batchSize;

    public List<UserDto> findUsersByPostId(Long postId) {
        var userIds = likeRepository.findByPostId(postId).stream()
                .map(Like::getUserId)
                .toList();
        checkUserIdListEmpty(userIds, postId);

        return getUsersInBatches(userIds);
    }

    public List<UserDto> findUsersByCommentId(Long commentId) {
        var userIds = likeRepository.findByCommentId(commentId).stream()
                .map(Like::getUserId)
                .toList();
        checkUserIdListEmpty(userIds, commentId);

        return getUsersInBatches(userIds);
    }

    private List<UserDto> getUsersInBatches(List<Long> userIds) {
        List<UserDto> allUsers = new ArrayList<>();

        for (int i = 0; i < userIds.size(); i += batchSize) {
            int end = min(i + batchSize, userIds.size());
            var batch = userIds.subList(i, end);
            var batchUsers = userServiceClient.getUsersByIds(batch);
            allUsers.addAll(batchUsers);
        }
        return allUsers;
    }

    private void checkUserIdListEmpty(List<Long> userIds, Long id) {
        if (userIds.isEmpty()) {
            throw new EntityNotFoundException("No users found for ID " + id);
        }
    }
}
