package faang.school.postservice.mapper.like;

import faang.school.postservice.dto.like.LikeEventDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import org.springframework.stereotype.Component;

@Component
public class LikeEventMapper {
    public LikeEventDto toEvent(Like like, Post post) {

        return LikeEventDto.builder()
                .postId(post.getId())
                .authorId(post.getAuthorId())
                .userExciterId(like.getUserId())
                .createdAt(like.getCreatedAt())
                .build();

    }

}
