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
    private static final int BATCH_SIZE = 100;

    public List<UserDto> getPostLikes(long id) {
        List<Like> likes = likeRepository.findByPostId(id);
        List<Long> ids = likes.stream().map(Like::getUserId).toList();

        return retrieveUsersByIds(ids);
    }

    public List<UserDto> getCommentLikes(long id) {
        List<Like> likes = likeRepository.findByCommentId(id);
        List<Long> ids = likes.stream().map(Like::getUserId).toList();

        return retrieveUsersByIds(ids);
    }

    private List<UserDto> retrieveUsersByIds(List<Long> ids) {
        List<UserDto> res = new ArrayList<>(ids.size());
        for (int i = 0; i < ids.size(); i += BATCH_SIZE) {
            int p = i + BATCH_SIZE;
            if (p > ids.size()) {
                p = ids.size();
            }
            List<Long> subIds = ids.subList(i, p);
            res.addAll(userServiceClient.getUsersByIds(subIds));
        }

        return res;
    }
}
