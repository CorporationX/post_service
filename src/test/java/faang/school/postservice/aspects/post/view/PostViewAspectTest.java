package faang.school.postservice.aspects.post.view;

import faang.school.postservice.annotations.PublishPostEvent;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.event.Event;
import faang.school.postservice.model.event.post.factory.PostViewEventFactory;
import faang.school.postservice.model.event.post.view.AnalyticsPostViewEvent;
import faang.school.postservice.model.event.post.view.NotificationPostViewEvent;
import faang.school.postservice.service.event.PostViewEventBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostViewAspectTest {

    @Mock
    private UserContext userContext;
    @Mock
    private PostViewEventFactory postViewEventFactory;
    @Mock
    private PostViewEventBuffer postViewEventBuffer;
    @Mock
    private JoinPoint joinPoint;
    @Mock
    private Signature signature;
    @Mock
    private PublishPostEvent publishPostEvent;

    @InjectMocks
    private PostViewAspect postViewAspect;

    private Post createValidPost(Long id, Long authorId) {
        Post post = new Post();
        post.setId(id);
        post.setAuthorId(authorId);
        post.setDeleted(false);
        return post;
    }

    @Test
    void testPublishEvent_SinglePost_EventsAddedToBuffer() {
        Post post = createValidPost(1L, 456L);
        when(userContext.getUserId()).thenReturn(123L);
        when(publishPostEvent.events()).thenReturn(new Class[]{AnalyticsPostViewEvent.class, NotificationPostViewEvent.class});
        when(postViewEventFactory.createEvents(any(), any(), any()))
                .thenReturn(List.of(new AnalyticsPostViewEvent(), new NotificationPostViewEvent()));

        postViewAspect.publishEvent(joinPoint, publishPostEvent, post);

        verify(postViewEventBuffer, times(2)).add(any(Event.class));
    }

    @Test
    void testPublishEvent_ListOfPosts_EventsAddedForEachPost() {
        List<Post> posts = List.of(
                createValidPost(1L, 456L),
                createValidPost(2L, 789L)
        );
        when(userContext.getUserId()).thenReturn(123L);
        when(publishPostEvent.events()).thenReturn(new Class[]{AnalyticsPostViewEvent.class});
        when(postViewEventFactory.createEvents(any(), any(), any()))
                .thenReturn(List.of(new AnalyticsPostViewEvent()));

        postViewAspect.publishEvent(joinPoint, publishPostEvent, posts);

        verify(postViewEventBuffer, times(2)).add(any(Event.class));
    }

    @Test
    void testPublishEvent_NullResult_NoEventsPublished() {
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("testMethod");

        postViewAspect.publishEvent(joinPoint, publishPostEvent, null);

        verifyNoInteractions(postViewEventBuffer);
    }

    @Test
    void testPublishEvent_PostAuthorIsViewer_SkipEvent() {
        Post post = createValidPost(1L, 123L);
        when(userContext.getUserId()).thenReturn(123L);

        postViewAspect.publishEvent(joinPoint, publishPostEvent, post);

        verifyNoInteractions(postViewEventFactory);
        verifyNoInteractions(postViewEventBuffer);
    }

    @Test
    void testPublishEvent_DeletedPost_SkipEvent() {
        Post post = createValidPost(1L, 456L);
        post.setDeleted(true);
        when(userContext.getUserId()).thenReturn(123L);

        postViewAspect.publishEvent(joinPoint, publishPostEvent, post);

        verifyNoInteractions(postViewEventFactory);
        verifyNoInteractions(postViewEventBuffer);
    }
}
