package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.LikeDto;
import org.springframework.stereotype.Service;

@Service
public interface LikeService {

    LikeDto likeToPost(LikeDto likeDto);
    void unlikeFromPost(LikeDto likeDto);
    LikeDto likeToComment(LikeDto likeDto);
    void unlikeFromComment(LikeDto likeDto);
}
