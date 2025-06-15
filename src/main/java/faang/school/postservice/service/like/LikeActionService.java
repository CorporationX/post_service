package faang.school.postservice.service.like;

import faang.school.postservice.dto.event.LikeEventDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.service.publisher.like.LikeEventPublisher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class LikeActionService {

    private final LikeEventPublisher likeEventPublisher;

    public LikeActionService(@Qualifier("redisLikeEventPublisher") LikeEventPublisher likeEventPublisher) {
        this.likeEventPublisher = likeEventPublisher;
    }

    public void registerLikePost(Like like) {
        if (like.getUserId() == null || like.getUserId().equals(like.getPost().getAuthorId())) {
            return;
        }

        LikeEventDto event = LikeEventDto.builder()
                .postAuthorId(like.getPost().getAuthorId())
                .likerId(like.getUserId())
                .postId(like.getPost().getId())
                .build();

        likeEventPublisher.publish(event);
    }
}
