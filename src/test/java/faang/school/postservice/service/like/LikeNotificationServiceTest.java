package faang.school.postservice.service.like;

import faang.school.postservice.dto.like.LikePostEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.LikeEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LikeNotificationServiceTest {
    @Mock
    private LikeEventPublisher likeEventPublisher;

    @InjectMocks
    private LikeNotificationService likeNotificationService;

    private final Post post = new Post();
    private static final long POST_ID = 1L;
    private static final long AUTHOR_ID = 2L;
    private static final long LIKER_ID = 3L;

    @BeforeEach
    public void setUp() {
        post.setId(POST_ID);
    }

    @Test
    @DisplayName("Публикация события лайка поста - успешный сценарий")
    public void givenPostWithAuthor_WhenPublishUserLikeEvent_ThenEventIsPublished() {
        post.setAuthorId(AUTHOR_ID);

        likeNotificationService.publishUserLikeEvent(post, LIKER_ID);

        verify(likeEventPublisher, times(1)).publish(any(LikePostEvent.class));
    }

    @Test
    @DisplayName("Публикация события лайка поста - пост без автора")
    public void givenPostWithoutAuthor_WhenPublishUserLikeEvent_ThenEventNotPublished() {
        likeNotificationService.publishUserLikeEvent(post, LIKER_ID);

        verify(likeEventPublisher, never()).publish(any());
    }
}
