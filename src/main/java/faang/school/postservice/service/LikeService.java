package faang.school.postservice.service;

import faang.school.postservice.model.dto.LikeDto;
import faang.school.postservice.model.dto.UserDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LikeService {
    List<UserDto> getAllUsersLikedPost(long postId);

    List<UserDto> getAllUsersLikedComment(long commentId);

    LikeDto addLikeToPost(Long postId, LikeDto likeDto);

    LikeDto removeLikeFromPost(Long postId, LikeDto likeDto);

    LikeDto addLikeToComment(Long commentId, LikeDto likeDto);

    LikeDto removeLikeFromComment(Long commentId, LikeDto likeDto);

    List<Long> getLikesFromPost(Long postId);

    List<Long> getLikesFromComment(Long commentId);
}
