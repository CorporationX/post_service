package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.LikePostEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.LikeEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeNotificationService {
    private final LikeEventPublisher likeEventPublisher;
    /**
     * Публикует событие о лайке поста, поставленного пользователем.
     *
     * @param post Лайкнутый пост
     * @param likerId ID пользователя, поставившего лайк
     */
    public void publishUserLikeEvent(Post post, long likerId) {
        long postId = post.getId();
        Long authorId = post.getAuthorId();

        if (authorId != null) {
            LikePostEvent likePostEvent = new LikePostEvent(authorId, likerId, postId);
            likeEventPublisher.publish(likePostEvent);
        }
    }
}
