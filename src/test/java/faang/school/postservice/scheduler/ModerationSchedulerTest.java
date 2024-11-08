package faang.school.postservice.scheduler;

import faang.school.postservice.model.entity.Post;
import faang.school.postservice.model.event.RateDecreaseEvent;
import faang.school.postservice.publisher.RateDecreaseEventPublisher;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModerationSchedulerTest {

    @Mock
    private PostService postService;
    @Mock
    private RateDecreaseEventPublisher rateDecreaseEventPublisher;

    @InjectMocks
    private ModerationScheduler moderationScheduler;

    @Test
    @DisplayName("Should collect all user IDs from multiple batches and publish event")
    void testVerifyPosts_MultipleBatches() {
        List<Post> batch1 = List.of(
                Post.builder().id(1L).content("Bad content 1").authorId(101L).build(),
                Post.builder().id(2L).content("Clean content").authorId(102L).build()
        );
        List<Post> batch2 = List.of(
                Post.builder().id(3L).content("Bad content 2").authorId(103L).build(),
                Post.builder().id(4L).content("Another clean content").authorId(104L).build()
        );

        when(postService.findAndSplitUnverifiedPosts()).thenReturn(List.of(batch1, batch2));

        doAnswer(invocation -> {
            Set<Long> usersWithImproperContent = invocation.getArgument(1);
            usersWithImproperContent.add(101L);
            return CompletableFuture.completedFuture(null);
        }).when(postService).verifyPostsForSwearWords(eq(batch1), anySet());

        doAnswer(invocation -> {
            Set<Long> usersWithImproperContent = invocation.getArgument(1);
            usersWithImproperContent.add(103L);
            return CompletableFuture.completedFuture(null);
        }).when(postService).verifyPostsForSwearWords(eq(batch2), anySet());

        moderationScheduler.verifyPosts();

        ArgumentCaptor<RateDecreaseEvent> eventCaptor = ArgumentCaptor.forClass(RateDecreaseEvent.class);
        verify(rateDecreaseEventPublisher, times(1)).publish(eventCaptor.capture());

        RateDecreaseEvent capturedEvent = eventCaptor.getValue();
        assertEquals("expletives", capturedEvent.getTitle());
        List<Long> expectedUserIds = List.of(101L, 103L);
        Assertions.assertTrue(capturedEvent.getUserIds().
                containsAll(expectedUserIds) && expectedUserIds.containsAll(capturedEvent.getUserIds()));

        verify(postService, times(1)).verifyPostsForSwearWords(eq(batch1), anySet());
        verify(postService, times(1)).verifyPostsForSwearWords(eq(batch2), anySet());
    }

    @Test
    @DisplayName("Should not publish event if no users with improper content")
    void testVerifyPosts_NoImproperContent() {
        List<Post> batch1 = List.of(
                Post.builder().id(1L).content("Clean content 1").authorId(101L).build(),
                Post.builder().id(2L).content("Another clean content").authorId(102L).build()
        );

        when(postService.findAndSplitUnverifiedPosts()).thenReturn(List.of(batch1));

        doAnswer(invocation -> CompletableFuture.completedFuture(null))
                .when(postService).verifyPostsForSwearWords(eq(batch1), anySet());

        moderationScheduler.verifyPosts();

        verify(rateDecreaseEventPublisher, never()).publish(any());
    }
}
