package faang.school.postservice.scheduler.ban;

import faang.school.postservice.event.ban.UserBanEvent;
import faang.school.postservice.publisher.ban.UserBanMessagePublisher;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthorBannerTest {

    private static final List<Long> AUTHORS_WITH_UNVERIFIED_POSTS = List.of(1L, 2L, 3L);

    @Mock
    private PostService postService;

    @Mock
    private UserBanMessagePublisher userBanMessagePublisher;

    @InjectMocks
    private AuthorBanner authorBanner;

    @Test
    @DisplayName("Should publish UserBanEvent for authors with unverified posts exceeding the limit")
    void shouldPublishUserBanEvents() {
        when(postService.findAuthorsWithUnverifiedPosts(anyInt()))
                .thenReturn(AUTHORS_WITH_UNVERIFIED_POSTS);

        authorBanner.publishUsersBanMessage();

        for (Long authorId : AUTHORS_WITH_UNVERIFIED_POSTS) {
            verify(userBanMessagePublisher).publish(new UserBanEvent(authorId));
        }
    }
}
