package faang.school.postservice.mapper;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LikeEventMapper {
    private final UserContext userContext;

    public LikeEvent mapLikeEvent(LikeDto likeDto) {
        return LikeEvent.builder()
                .postId(likeDto.getPostId())
                .authorLikeId(userContext.getUserId())
                .userId(likeDto.getUserId())
                .localDateTime(LocalDateTime.now())
                .build();
    }
}