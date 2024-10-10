package faang.school.postservice.dto.event.post;

import faang.school.postservice.dto.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PostViewedEvent extends Event {
    private final Long postId;
    private final Long currentViews;
}
