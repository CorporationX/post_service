package faang.school.postservice.model.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EventType {
    PUBLISHED_POST("publishedPost"),
    LIKED_POST("likedPost");

    private final String key;
}