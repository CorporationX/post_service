package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.adapter.CommentRepositoryAdapter;
import faang.school.postservice.repository.adapter.PostRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    private final UserServiceClient userServiceClient;

    private final LikeRepository likeRepository;

    private final PostRepositoryAdapter postRepositoryAdapter;
    private final CommentRepositoryAdapter commentRepositoryAdapter;

    @Value("${like.batch}")
    private int likeBatch;

    public List<UserDto> getUsersWhoLikedPost(long postId) {
        postRepositoryAdapter.getById(postId);

        List<Long> userIds = likeRepository.findUserIdsByPostId(postId);
        List<UserDto> users = getUsers(userIds);

        log.info("IDs of the users who liked the post with ID {}: {}", postId, users);
        return users;
    }

    public List<UserDto> getUsersWhoLikedComment(long commentId) {
        commentRepositoryAdapter.getById(commentId);

        List<Long> userIds = likeRepository.findUserIdsByCommentId(commentId);
        List<UserDto> users = getUsers(userIds);

        log.info("IDs of the users who liked the comment with ID {}: {}", commentId, users);
        return users;
    }

    private List<UserDto> getUsers(List<Long> userIds) {
        List<UserDto> users = new ArrayList<>();
        for (int i = 0; i < userIds.size(); i += likeBatch) {
            int toIndex = Math.min(i + likeBatch, userIds.size());
            List<Long> sublistOfUserIds = userIds.subList(i, toIndex);
            users.addAll(userServiceClient.getUsersByIds(sublistOfUserIds));
        }
        return users;
    }
}
