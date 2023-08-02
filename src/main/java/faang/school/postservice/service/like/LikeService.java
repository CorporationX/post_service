package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private static final int BATCH_SIZE = 100;

    public List<UserDto> getUsersByPostId(long postId) {
        List<Long> userIds = getLikedUserIdsByPost(postId);
        return getAllUsersDto(userIds);
    }

    public List<UserDto> getUsersByCommentId(long commentId) {
        List<Long> userIds = getLikedUserIdsByComment(commentId);
        return getAllUsersDto(userIds);
    }

    private List<Long> getLikedUserIdsByPost(long postId) {
        return likeRepository.findByPostId(postId).stream()
                .map(Like::getUserId)
                .toList();
    }

    private List<Long> getLikedUserIdsByComment(long commentId) {
        return likeRepository.findByCommentId(commentId).stream()
                .map(Like::getUserId)
                .toList();
    }

    private List<UserDto> getAllUsersDto(List<Long> userIds) {
        List<UserDto> allUsers = new ArrayList<>();
        for (int i = 0; i < userIds.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, userIds.size());
            allUsers.add(userServiceClient.getUsersByIds(userIds.subList(i, endIndex)));
        }
        return allUsers;
    }
}
