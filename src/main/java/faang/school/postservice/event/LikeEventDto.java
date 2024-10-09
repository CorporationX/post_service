package faang.school.postservice.event;

public record LikeEventDto(long postAuthorId,
                           long likeAuthorId,
                           long postId) {

}
