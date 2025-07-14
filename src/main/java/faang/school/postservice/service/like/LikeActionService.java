package faang.school.postservice.service.like;

import faang.school.postservice.dto.event.LikeEventDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.service.publisher.like.LikeEventPublisher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class LikeActionService {

    private final LikeEventPublisher likeEventPublisher;
    private final LikeMapper likeMapper;

    public LikeActionService(@Qualifier("redisLikeEventPublisher") LikeEventPublisher likeEventPublisher,
                             LikeMapper likeMapper) {
        this.likeEventPublisher = likeEventPublisher;
        this.likeMapper = likeMapper;
    }

    public void registerLikePost(Like like) {
        if (like.getUserId() == null || like.getUserId().equals(like.getPost().getAuthorId())) {
            return;
        }
        LikeEventDto event = likeMapper.toEventDto(like);
        likeEventPublisher.publish(event);
    }
}
