package faang.school.postservice.service.like;

import faang.school.postservice.dto.user.UserDto;

import java.util.List;

/**
 * LikeService — описание интерфейса.
 *
 * @author bozya
 * @since 12.08.2025
 */

public interface LikeService {
    List<UserDto> getUsersWhoLikedPost(Long postId);

    List<UserDto> getUsersWhoLikedComment(Long postId, Long commentId);
}