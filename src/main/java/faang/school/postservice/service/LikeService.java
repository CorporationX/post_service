package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final CommentService commentService;
    private final PostService postService;

    @Value("${feign.maxUsersSubListSize}")
    private int maxSubListSize;


    @Transactional
    public List<UserDto> getUsersWhoLikesByPostId(Long postId) {
        List<Like> likes = likeRepository.findByPostId(postId);
        List<UserDto> result = findUsersWhoLiked(likes);
        log.info("Corrected {} users", result.size());
        return result;
    }

    @Transactional
    public List<UserDto> getUsersWhoLikesByCommentId(Long commentId) {
        List<Like> likes = likeRepository.findByCommentId(commentId);
        List<UserDto> result = findUsersWhoLiked(likes);
        log.info("Corrected {} users", result.size());
        return result;
    }

    private List<UserDto> findUsersWhoLiked(List<Like> likes) {
        List<Long> usersId = likes.stream().map(Like::getUserId).toList();
        List<UserDto> result = new ArrayList<>();
        for (int i = 0; i < usersId.size(); i += maxSubListSize) {
            List<Long> subList = new ArrayList<>();
            subList = usersId.subList(i, Math.min(i + maxSubListSize, usersId.size()));
            result.addAll(userServiceClient.getUsersByIds(subList));
        }
        return result;
    }
}
