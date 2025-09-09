package faang.school.postservice.events;

public interface CommentEventPublisher {
    void publishCommentEvent(CommentEvent event);
}
