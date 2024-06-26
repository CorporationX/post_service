package faang.school.postservice.service.comment;

import faang.school.postservice.dto.redis.event.UserEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.publisher.redis.userban.UserBanRedisPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;

import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommenterBannerTest {

    @Mock
    private CommentService commentService;

    @Mock
    private UserBanRedisPublisher userBanPublisher;

    @InjectMocks
    private CommenterBanner commenterBanner;

    @BeforeEach
    void init() throws Exception {
        Field countCommentsForBan = commenterBanner.getClass().getDeclaredField("countCommentsForBan");
        countCommentsForBan.setAccessible(true);
        countCommentsForBan.set(commenterBanner, 5);
    }

    @Test
    void sendUsersToBan_ValidArgs() {
        Long userId = 1L;
        when(commentService.findCommentsByVerified(anyBoolean())).thenReturn(getComments(userId));

        commenterBanner.sendUsersToBan();

        verify(commentService, times(1)).findCommentsByVerified(anyBoolean());
        verify(userBanPublisher, times(1)).publish(new UserEvent(userId));
    }

    private List<Comment> getComments(Long userId) {
        return List.of(
                Comment.builder()
                        .authorId(userId)
                        .verified(false)
                        .build(),
                Comment.builder()
                        .authorId(userId)
                        .verified(false)
                        .build(),
                Comment.builder()
                        .authorId(userId)
                        .verified(false)
                        .build(),
                Comment.builder()
                        .authorId(userId)
                        .verified(false)
                        .build(),
                Comment.builder()
                        .authorId(userId)
                        .verified(false)
                        .build(),
                Comment.builder()
                        .authorId(userId)
                        .verified(false)
                        .build()
        );
    }
}
