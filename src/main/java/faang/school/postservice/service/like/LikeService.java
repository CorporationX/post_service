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

    public List<UserDto> getAllUsersByPostId(long id) {
        return getUsersBatched(getUsersIdsByLikes(getUsersIdsByPostId(id)));
    }

    public List<UserDto> getAllUsersByCommentId(long id) {
        return getUsersBatched(getUsersIdsByLikes(getUserIdsByCommentId(id)));
    }

    private List<Like> getUserIdsByCommentId(long id) {
        return likeRepository.findByCommentId(id);
    }

    private List<Like> getUsersIdsByPostId(long id) {
        return likeRepository.findByPostId(id);
    }

    private List<Long> getUsersIdsByLikes(List<Like> likes) {
        return likes.stream()
                .map(Like::getUserId)
                .toList();
    }

    private List<UserDto> getUsersBatched(List<Long> userIds) {
        long batchSize = userIds.size() / BATCH_SIZE + 1;
        List<UserDto> usersLiked = new ArrayList<>();
        for (int i = 0; i < batchSize; i += BATCH_SIZE) {
            int batchEnd = Math.min(i + BATCH_SIZE, userIds.size());
            usersLiked.addAll(userServiceClient.getUsersByIds(userIds.subList(i, batchEnd)));
        }
        return usersLiked;
    }
}
