package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final UserServiceClient client;

    @Override
    public List<UserDto> getUsersLikedPost(long postId) {
        List<Long> userIds = likeRepository.findByPostId(postId).stream()
                .map(Like::getUserId)
                .toList();

        return getUsers(userIds);
    }

    @Override
    public List<UserDto> getUsersLikedComm(long postId) {
        List<Long> userIds = likeRepository.findByCommentId(postId).stream()
                .map(Like::getUserId)
                .toList();

        return getUsers(userIds);
    }

    /**
     * Отправляет запросы на получение списка UserDto по списку id
     * user_service по 100 штук, и возвращает полный список UserDto
     */
    private List<UserDto> getUsers(List<Long> userIds) {
        List<UserDto> response = new ArrayList<>();

        int remains = userIds.size() % 100;
        int whole = userIds.size() / 100;

        try {
            for (int i = 0; i < whole; i++) {
                response.addAll(client.getUsersByIds(userIds.subList(i * 100, i * 100 + 100)));
            }
            if (remains > 0) {
                response.addAll(client.getUsersByIds(userIds.subList(whole * 100, whole * 100 + remains)));
            }
        } catch (Exception e) {
            log.warn("request to user_service did not send");
        }

        return response;
    }
}
