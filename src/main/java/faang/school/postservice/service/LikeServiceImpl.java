package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private static final int BATCH_SIZE = 100;

    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;

    @Transactional
    @Override
    public List<UserDto> getUsersWhoLikedPost(Long postId) {
        List<Like> likes = likeRepository.findByPostId(postId);
        return getUsersByBatches(likes.stream()
                .map(Like::getUserId)
                .toList());
    }

    @Transactional
    @Override
    public List<UserDto> getUsersWhoLikedComment(Long commentId) {
        List<Like> likesByCommentId = likeRepository.findByCommentId(commentId);
        return getUsersByBatches(likesByCommentId.stream()
                .map(Like::getUserId)
                .toList());
    }

    private List<UserDto> getUsersByBatches(List<Long> userIds) {
        List<UserDto> users = new ArrayList<>();
        for (int batchStart = 0; batchStart < userIds.size(); batchStart += BATCH_SIZE) {
            int batchEnd = Math.min(batchStart + BATCH_SIZE, userIds.size());
            List<Long> batchIds = userIds.subList(batchStart, batchEnd);
            users.addAll(userServiceClient.getUsersByIds(batchIds));
        }
        return users;
    }
}
