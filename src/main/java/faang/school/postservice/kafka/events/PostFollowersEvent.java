package faang.school.postservice.kafka.events;

import lombok.Builder;

import java.util.List;

@Builder
public record PostFollowersEvent(//TODO should be FeedDto
        Long authorId,
        Long postId,//TODO delete!!!
        List<Long> followersIds
) {}