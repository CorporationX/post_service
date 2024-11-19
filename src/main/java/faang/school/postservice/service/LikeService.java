package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class LikeService {
    private static final int BATCH_SIZE = 100;
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;

    public List<UserDto> getUsersWhoLikePostByPostId(@NotNull @Positive long Id) {
        List<Like> usersWhoLikedPost  = likeRepository.findByPostId(Id);
        return getUserDtos(usersWhoLikedPost);
    }


    public List<UserDto> getCommentLikers(@NotNull @Positive long id) {
        List<Like> usersWhoLikedComment  = likeRepository.findByCommentId(id);
        return getUserDtos(usersWhoLikedComment);
    }


    private List<UserDto> getUserDtos(@NotNull @Positive List<Like> usersWhoLiked) {
        List<Long> userIds = usersWhoLiked.stream()
                .map(Like::getUserId)
                .toList();

        List<List<Long>> userIdBatches = new ArrayList<>();

        for (int i = 0; i < userIds.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, userIds.size());
            userIdBatches.add(userIds.subList(i, endIndex));
        }

        List<UserDto> userDtos = new ArrayList<>();

        for (List<Long> batch : userIdBatches) {
            List<UserDto> batchResults = userServiceClient.getUsersByIds(batch);
            userDtos.addAll(batchResults);
        }

        return userDtos;
    }
}
