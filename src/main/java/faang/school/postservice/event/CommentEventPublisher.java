package faang.school.postservice.event;

public interface CommentEventPublisher {
    void publishCommentEvent(CommentEvent event);
}
