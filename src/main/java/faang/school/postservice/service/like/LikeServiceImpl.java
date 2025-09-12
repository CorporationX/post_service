package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Сервис для работы с лайками постов и комментариев.
 * <p>
 * Предоставляет методы для получения информации о пользователях, поставивших лайки
 * на посты и комментарии. Включает валидацию входных параметров и обработку
 * ошибок при работе с репозиториями.
 * </p>
 *
 * @author bozya
 * @since 12.08.2025
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final UserServiceClient serviceClient;

    @Override
    public List<UserDto> getUsersWhoLikedPost(Long postId) {
        List<Like> likes = likeRepository.findByPostId(postId);

        List<Long> userIds = likes.stream()
                .map(Like::getUserId)
                .filter(Objects::nonNull)
                .toList();

        return serviceClient.getUsersByIds(userIds);
    }

    @Override
    public List<UserDto> getUsersWhoLikedComment(Long commentId) {
        List<Like> likes = likeRepository.findByCommentId(commentId);

        List<Long> userIds = likes.stream()
                .map(Like::getUserId)
                .filter(Objects::nonNull)
                .toList();

        return serviceClient.getUsersByIds(userIds);
    }
}