package faang.school.postservice.model.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EventType {
    PUBLISHED_POST,
    LIKED_POST,
    COMMENT_CREATED,
    POST_VIEWS;
}