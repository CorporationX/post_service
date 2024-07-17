package faang.school.postservice.validator;

import faang.school.postservice.dto.like.LikeDto;
import org.springframework.stereotype.Component;

@Component
public class LikeControllerValidator {

    public void validAddLikeToPost(long userId, long postId) {

    }

    public void validDeleteLikeFromPost(LikeDto likeDto) {

    }

    public void validAddLikeTOComment(long userId, long commentId) {

    }

    public void validDeleteLikeFromComment(LikeDto likeDto) {

    }

    public void validGetCountLikeForPost(long postId) {

    }

}
