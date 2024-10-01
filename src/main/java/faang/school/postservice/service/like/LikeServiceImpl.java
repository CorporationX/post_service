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
public class LikeServiceImpl implements LikeService{

    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;

    @Override
    public List<UserDto> getUsersByPostId(long postId) {
        List<Like> likes = likeRepository.findByPostId(postId);

        return dividingListIntoGroups(likes);
    }

    @Override
    public List<UserDto> getUsersByCommentId(long commentId) {
        List<Like> likes = likeRepository.findByCommentId(commentId);

        return dividingListIntoGroups(likes);
    }

    private List<UserDto> dividingListIntoGroups(List<Like> likes) {
        List<Long> userIds = likes.stream()
                .map(Like::getUserId)
                .toList();

        List<List<Long>> subLists = new ArrayList<>();
        List<List<UserDto>> results = new ArrayList<>();

        for (int i = 0; i < userIds.size(); i += 100) {
            subLists.add(userIds.subList(i, Math.min(i + 100, userIds.size())));

            for (List<Long> subList : subLists) {
                List<UserDto> result = userServiceClient.getUsersByIds(subList);
                results.add(result);
            }
        }

        return results.stream()
                .flatMap(List::stream)
                .toList();
    }

}
