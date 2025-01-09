package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@RequiredArgsConstructor
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserServiceClient client;

    public List<UserDto> getUsersThatLikedThePost(long postId) {
        List<Like> likes = likeRepository.findByPostId(postId);

        return getUsers(likes);
    }

    public List<UserDto> getUsersThatLikedTheComment(long commentId) {
        List<Like> likes = likeRepository.findByCommentId(commentId);

        return getUsers(likes);
    }

    private List<UserDto> getUsers(List<Like> likes) {
        List<Long> userIds = likes
                .stream()
                .map(Like::getUserId)
                .toList();

        int batchSize = 100;
        List<List<Long>> batchedListOfUserIds =
                ListUtils.partition(userIds, batchSize);
        List<List<UserDto>> batchedListOfUserDtos = new ArrayList<>();

        for (List<Long> batch : batchedListOfUserIds) {
            List<UserDto> batchOfUserDtos = client.getUsersByIds(batch);
            batchedListOfUserDtos.add(batchOfUserDtos);
        }

        return batchedListOfUserDtos
                .stream()
                .flatMap(x -> x.stream())
                .toList();
    }

}
