package faang.school.postservice.dto.feed;

import faang.school.postservice.dto.redis.UserRedisDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO с данными поста для отображения в ленте новостей
 *
 * @author Linempy
 * @since 28.09.2025
 */
public record PostFeedDto(
        Long id,
        String content,
        Long projectId,
        UserRedisDto authorUser,
        List<CommentFeedDto> latestComments,
        Long likeCounter,
        Long commentCounter,
        LocalDateTime publishedAt
) {
}