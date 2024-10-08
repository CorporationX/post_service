package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ModerationJobTest {
    private ModerationJob moderationJob;

    @Mock
    private ModerationPostService moderationService;
    @Mock
    private JobExecutionContext jobExecutionContext;

    @BeforeEach
    void setUp() throws Exception {
        moderationJob = new ModerationJob(moderationService);

        Field sublistSizeField = ModerationJob.class.getDeclaredField("sublistSize");
        sublistSizeField.setAccessible(true);
        sublistSizeField.set(moderationJob, 2);

        Field threadPoolSizeField = ModerationJob.class.getDeclaredField("threadPoolSize");
        threadPoolSizeField.setAccessible(true);
        threadPoolSizeField.set(moderationJob, 2);

        Field awaitTerminationSecondsField = ModerationJob.class.getDeclaredField("awaitTerminationSeconds");
        awaitTerminationSecondsField.setAccessible(true);
        awaitTerminationSecondsField.set(moderationJob, 5);
    }

    @Test
    void testExecute()  {
        List<Post> unverifiedPosts = Arrays.asList(new Post(), new Post(), new Post(), new Post(), new Post());
        when(moderationService.findUnverifiedPosts()).thenReturn(unverifiedPosts);

        List<Post> sublist1 = Arrays.asList(unverifiedPosts.get(0), unverifiedPosts.get(1));
        List<Post> sublist2 = Arrays.asList(unverifiedPosts.get(2), unverifiedPosts.get(3));
        List<Post> sublist3 = Arrays.asList(unverifiedPosts.get(4));

        List<List<Post>> sublists = Arrays.asList(sublist1, sublist2, sublist3);
        when(moderationService.splitListIntoSublists(eq(unverifiedPosts), eq(2))).thenReturn(sublists);

        moderationJob.execute(jobExecutionContext);

        verify(moderationService, times(1)).findUnverifiedPosts();
        verify(moderationService, times(1)).splitListIntoSublists(eq(unverifiedPosts), eq(2));
        verify(moderationService, times(3)).moderatePostsSublist(anyList());
    }
}