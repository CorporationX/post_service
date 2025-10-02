package faang.school.postservice.dto.feed;

import faang.school.postservice.dto.comment.CommentViewDto;
import faang.school.postservice.dto.redis.UserRedisDto;
import io.swagger.v3.oas.annotations.media.Schema;

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
        List<CommentViewDto> comments,
        Long likeCounter,
        Long commentCounter,
        LocalDateTime publishedAt
) {
}