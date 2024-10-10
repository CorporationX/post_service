package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
class ScheduledPostPublisherTest {
    @Mock
    private PostService postService;
    @InjectMocks
    private ScheduledPostPublisher scheduledPostPublisher;

    private static final Integer SUB_LIST_SIZE = 2;

    private List<Post> posts = new ArrayList<>();
    private List<Post> subList1;
    private List<Post> subList2;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(scheduledPostPublisher, "subListSize", SUB_LIST_SIZE);

        Post post1 = Post.builder().id(1L).build();
        Post post2 = Post.builder().id(2L).build();
        Post post3 = Post.builder().id(3L).build();
        Post post4 = Post.builder().id(4L).build();
        posts.add(post1);
        posts.add(post2);
        posts.add(post3);
        posts.add(post4);
        subList1 = List.of(post1, post2);
        subList2 = List.of(post3, post4);

    }

    @Test
    void testScheduledPostPublish() {
        when(postService.getAllReadyToPublishPosts()).thenReturn(posts);
        scheduledPostPublisher.scheduledPostPublish();
        verify(postService).getAllReadyToPublishPosts();
        verify(postService, times(2)).processSubList(anyList());
        verify(postService).processSubList(subList1);
        verify(postService).processSubList(subList2);
    }
}