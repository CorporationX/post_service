package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.LikeDto;
import org.springframework.stereotype.Service;

@Service
public interface LikeService {
    LikeDto addCommentLike(LikeDto likeDto);

    void deleteCommentLike(Long userId, Long commentId);

    LikeDto addPostLike(LikeDto likeDto);

    void deletePostLike(Long userid, Long postId);
}
