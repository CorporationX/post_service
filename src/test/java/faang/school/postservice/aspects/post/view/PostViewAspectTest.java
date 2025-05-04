package faang.school.postservice.aspects.post.view;

import faang.school.postservice.annotations.PublishPostEvent;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.event.post.PostEventType;
import faang.school.postservice.publisher.post.PostEventPublisher;
import faang.school.postservice.service.post.view.PostResultParser;
import faang.school.postservice.validation.post.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostViewAspectTest {

    @Mock
    private UserContext userContext;

    @Mock
    private PostResultParser resultParser;

    @Mock
    private PostValidator postValidator;

    @Mock
    private PostEventPublisher eventPublisher;

    @InjectMocks
    private PostViewAspect postViewAspect;

    private TestService testServiceProxy;

    @BeforeEach
    void setUp() {
        AspectJProxyFactory factory = new AspectJProxyFactory(new TestService());
        factory.addAspect(postViewAspect);
        testServiceProxy = factory.getProxy();
    }

    @Test
    void testPublishEvent_WithSinglePost() {
        Post post = new Post();
        post.setAuthorId(2L);
        when(userContext.getUserId()).thenReturn(1L);
        when(resultParser.parseResult(post)).thenReturn(List.of(post));
        when(postValidator.shouldSkip(post, 1L)).thenReturn(false);

        testServiceProxy.testMethodWithAnnotation(post);

        verify(eventPublisher).publishEvents(eq(post), eq(1L), any());
    }

    @Test
    void testPublishEvent_WithPostList() {
        Post post1 = new Post();
        Post post2 = new Post();
        List<Post> posts = List.of(post1, post2);

        when(userContext.getUserId()).thenReturn(1L);
        when(resultParser.parseResult(posts)).thenReturn(posts);
        when(postValidator.shouldSkip(any(), eq(1L))).thenReturn(false);

        testServiceProxy.testMethodWithAnnotation(posts);

        verify(eventPublisher, times(2)).publishEvents(any(), eq(1L), any());
    }

    @Test
    void testPublishEvent_WhenShouldSkipPost() {
        Post post = new Post();
        when(userContext.getUserId()).thenReturn(1L);
        when(resultParser.parseResult(post)).thenReturn(List.of(post));
        when(postValidator.shouldSkip(post, 1L)).thenReturn(true);

        testServiceProxy.testMethodWithAnnotation(post);

        verify(eventPublisher, never()).publishEvents(any(), any(), any());
    }

    private static class TestService {

        public TestService() {}
        @PublishPostEvent(eventTypes = {PostEventType.ANALYTICS})
        public Object testMethodWithAnnotation(Object result) {
            return result;
        }
    }
}