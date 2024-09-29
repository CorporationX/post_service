package faang.school.postservice.kafka.events;

import lombok.Builder;

import java.util.List;

@Builder
public record PostFollowersEvent(//TODO should be FeedEvent
        Long authorId,
        Long postId,//TODO delete!!!
        List<Long> followersIds
) {}