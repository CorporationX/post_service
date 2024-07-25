package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {

    @Value("${batch-size}")
    @Setter
    private int BATCH_SIZE;

    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;

    public List<UserDto> getLikesUsersByPostId(Long postId) {

        List<Long> userIdsLikedPost = likeRepository.findByPostId(postId).stream()
                .map(Like::getUserId)
                .toList();

        return getUsersDto(userIdsLikedPost);
    }

    public List<UserDto> getLikesUsersByCommentId(Long commentId) {

        List<Long> userIdsLikedComment = likeRepository.findByCommentId(commentId).stream()
                .map(Like::getUserId)
                .toList();

        return getUsersDto(userIdsLikedComment);
    }

    private List<UserDto> getUsersDto(List<Long> userIdsLiked) {

        List<List<Long>> batchesList = new ArrayList<>();

        for (int i = 0; i < userIdsLiked.size(); i += BATCH_SIZE) {
            List<Long> batch = userIdsLiked.subList(i, Math.min(i + BATCH_SIZE, userIdsLiked.size()));
            batchesList.add(batch);
        }
        return collectUsersDto(batchesList);
    }

    private List<UserDto> collectUsersDto(List<List<Long>> batchesList) {

        List<UserDto> result = new ArrayList<>();

        for (List<Long> batch : batchesList) {
            result.addAll(userServiceClient.getUsersByIds(batch));
        }
        return result;
    }
}
