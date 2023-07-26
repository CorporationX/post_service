package faang.school.postservice.controller.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.like.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    public LikeDto makeLikeForPost (long postId, LikeDto likeDto) {
        if (likeDto.getPostId() == null ) {
            throw new DataValidationException("PostId cannot be empty");
        } else {
            return likeService.createLikeForPost(postId, likeDto);
        }
    }
}
