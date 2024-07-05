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
public class LikeService {
    private static final int USER_BATCH_SIZE = 100;
    private final UserServiceClient userServiceClient;
    private final LikeRepository likeRepository;

    @Transactional(readOnly = true)
    public List<UserDto> getAllLikedPost(Long postId) {
        List<Long> usersId = getUserIds(likeRepository.findByPostId(postId));
        return fetchedUsers(usersId);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllLikedComment(Long commentId) {
        List<Long> usersId = getUserIds(likeRepository.findByCommentId(commentId));
        return fetchedUsers(usersId);
    }

    private List<UserDto> fetchedUsers(List<Long> usersId) {
        List<UserDto> allUserLikedPost = new ArrayList<>();

        for (int i = 0; i < usersId.size(); i += USER_BATCH_SIZE) {
            var idx = Math.min(i + USER_BATCH_SIZE, usersId.size());
            List<UserDto> users = userServiceClient.getUsersByIds(usersId.subList(i, idx));
            allUserLikedPost.addAll(users);
        }
        return allUserLikedPost;
    }

    private List<Long> getUserIds(List<Like> likes) {
        return likes.stream()
                .map(Like::getUserId)
                .toList();
    }
}
