package faang.school.postservice.util;

import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.mapper.LikeEventMapper;
import faang.school.postservice.model.Like;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeEventCreator {
    private final LikeEventMapper likeEventMapper;

    public LikeEvent create(Like like) {
        return likeEventMapper.likeToEvent(like);
    }
}