package faang.school.postservice.service;

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

    public List<UserDto> getAllUsersByPostId(long id) {
        List<Long> userIds = getUsersIdsByPostId(id);
        return getUserDtoListByIdsBatched(userIds);
    }

    public List<UserDto> getAllUsersByCommentId(long id) {
        List<Long> userIds = getUserIdsByCommentId(id);
        return getUserDtoListByIdsBatched(userIds);
    }

    private List<Long> getUserIdsByCommentId(long id) {
        List<Like> likes = likeRepository.findByCommentId(id);
        return likes.stream()
                .map(Like::getUserId)
                .toList();
    }

    private List<Long> getUsersIdsByPostId(long id) {
        List<Like> likes = likeRepository.findByPostId(id);
        return likes.stream()
                .map(Like::getUserId)
                .toList();
    }

    private List<UserDto> getUserDtoListByIdsBatched(List<Long> userIds) {
        long batchSize = userIds.size() / 100 + 1;
        List<UserDto> usersLiked = new ArrayList<>();
        for (int i = 0; i < batchSize; i += 100) {
            int batchEnd = Math.min(i + 100, userIds.size());
            usersLiked.addAll(userServiceClient.getUsersByIds(userIds.subList(i, batchEnd)));
        }
        return usersLiked;
    }
}
