package faang.school.postservice.conroller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class LikeController {
    private final LikeService likeService;

    public void addLikeToPost(LikeDto likeDto, long postId) {
        likeService.addLikeToPost(likeDto, postId);
    }

    public void deleteLikeFromPost(LikeDto likeDto, long postId) {
        likeService.deleteLikeFromPost(likeDto, postId);
    }

    public void addLikeToComment(LikeDto likeDto, long commentId) {
        likeService.addLikeToComment(likeDto, commentId);
    }

    public void deleteLikeFromComment(LikeDto likeDto, long commentId) {
        likeService.deleteLikeFromComment(likeDto, commentId);
    }
    public List<Like> findLikesOfPublishedPost(long postId) {
        return likeService.findLikesOfPublishedPost(postId);
    }

}
