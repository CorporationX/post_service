package faang.school.postservice.facade.like;

import faang.school.postservice.dto.like.LikeResponseDto;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.service.like.LikeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LikeFacade {
    private LikeService likeService;
    private LikeMapper likeMapper;

    public LikeResponseDto addLikeToPost(long postId) {
        Like like = likeService.addLikeToPost(postId);
        return likeMapper.toLikeResponseDto(like);
    }

    public LikeResponseDto addLikeToComment(long commentId) {
        Like like = likeService.addLikeToComment(commentId);
        return likeMapper.toLikeResponseDto(like);
    }

    public void deleteLikeFromPost(long postId) {
        likeService.deleteLikeFromPost(postId);
    }

    public void deleteLikeFromComment(long commentId) {
        likeService.deleteLikeFromComment(commentId);
    }
}
