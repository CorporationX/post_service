package faang.school.postservice.sheduler;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ScheduledPostPublisherTest {
    @InjectMocks
    private ScheduledPostPublisher scheduledPostPublisher;
    @Mock
    private PostService postService;
    @Spy
    private ConcurrentHashMap<LocalDateTime, Set<PostDto>> postMap;
    private int quantityElementInList = 5;


    @BeforeEach
    public void setUp() {
        scheduledPostPublisher= new ScheduledPostPublisher(postService, postMap);
        ReflectionTestUtils.setField(scheduledPostPublisher, "quantityElements", 3);
    }

    @Test
    void testPublishPosts() throws InterruptedException {
        ConcurrentHashMap<LocalDateTime, Set<PostDto>> postMapMock = Mockito.mock(ConcurrentHashMap.class);
        scheduledPostPublisher = new ScheduledPostPublisher(postService, postMapMock);

        Set<PostDto> postDtoSet = new HashSet<>();

        for (long i = 0; i < quantityElementInList; i++) {
            postDtoSet.add(PostDto.builder()
                    .id(i)
                    .authorId(i)
                    .build());
        }

        Mockito.when(postMapMock.get(any(LocalDateTime.class)))
                .thenReturn(postDtoSet);

        scheduledPostPublisher.publishPosts();

        Thread.sleep(1000);
        Mockito.verify(postService, Mockito.times(quantityElementInList))
                .publishPost(any(Long.class), any(Long.class));
        Mockito.verify(postMapMock, Mockito.times(1))
                .get(any(LocalDateTime.class));
        Mockito.verify(postMapMock, Mockito.times(1))
                .remove(any(LocalDateTime.class));
    }

    @Test
    void testUpdateCachePost() throws InterruptedException {
        List<PostDto> postDtoList = new ArrayList<>();

        for (long i = 0; i < quantityElementInList; i++) {
            postDtoList.add(PostDto.builder()
                    .id(i)
                    .authorId(i)
                    .scheduledAt(LocalDateTime.now().plus(1, ChronoUnit.MINUTES))
                    .build());
        }

        Mockito.when(postService.findAllPostsByTimeAndStatus())
                .thenReturn(postDtoList);

        scheduledPostPublisher.updateCachePost();

        Thread.sleep(1000);
        Mockito.verify(postMap, Mockito.times(quantityElementInList))
                .putIfAbsent(any(LocalDateTime.class), any());
        Mockito.verify(postMap, Mockito.times(quantityElementInList))
                .get(any(LocalDateTime.class));
        assertTrue(!postMap.isEmpty());
    }
}