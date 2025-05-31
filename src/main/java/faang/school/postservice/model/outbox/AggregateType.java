package faang.school.postservice.model.outbox;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AggregateType {

    POST("posts"),
    LIKE("likes"),
    COMMENT("comments"),
    POST_VIEW("postViews");

    private final String topic;
}
