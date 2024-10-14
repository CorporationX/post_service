package faang.school.postservice.scheduler;

import faang.school.postservice.service.impl.comment.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommenterBannerTest {

    @Mock
    private CommentServiceImpl commentService;

    @InjectMocks
    private CommenterBanner commenterBanner;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(commenterBanner, "unverifiedCommentsLimit", 5);
    }

    @Test
    public void testScheduleCommentersBanCheck() {
        // Act
        commenterBanner.scheduleCommentersBanCheck();

        // Assert
        verify(commentService, times(1)).commentersBanCheck(5);
    }

}
