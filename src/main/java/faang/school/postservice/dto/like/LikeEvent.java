package faang.school.postservice.dto.like;

public record LikeEvent (Long postUserId, Long likeUserId, Long postId){}
