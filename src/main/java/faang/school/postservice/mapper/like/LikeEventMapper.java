package faang.school.postservice.mapper.like;

import faang.school.postservice.dto.like.AbstractLikeEvent;
import faang.school.postservice.dto.like.PostLikeEventDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.springframework.stereotype.Component;

@Component
public class LikeEventMapper {

    public PostLikeEventDto toPostLikeEvent(Like like, Post post) {
        return new PostLikeEventDto(post.getId(), post.getAuthorId(), like.getUserId());
    }

}
