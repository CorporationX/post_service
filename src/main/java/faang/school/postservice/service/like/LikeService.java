package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LikeService {
    LikeDto addCommentLike(LikeDto likeDto);

    void deleteCommentLike(LikeDto likeDto);

    LikeDto addPostLike(LikeDto likeDto);

    void deletePostLike(LikeDto likeDto);
    List<UserDto> findUsersByPostId(Long postId);
    List<UserDto> findUsersByCommentId(Long commentId);
}
