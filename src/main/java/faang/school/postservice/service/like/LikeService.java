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
        List<UserDto> allUsers = new ArrayList<>();
        List<Long> userIds = getLikedUserIdsByPost(postId);

        for (int i = 0; i < userIds.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, userIds.size());
            allUsers.add(userServiceClient.getUsersByIds(userIds.subList(i, endIndex)));
        }

        return allUsers;
    }

    private List<Long> getLikedUserIdsByPost(long postId) {
        return likeRepository.findByPostId(postId).stream()
                .map(Like::getUserId)
                .toList();
    }
}
