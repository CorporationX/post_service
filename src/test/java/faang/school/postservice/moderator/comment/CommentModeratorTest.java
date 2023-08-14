package faang.school.postservice.moderator.comment;

import faang.school.postservice.service.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@EnableScheduling
@TestPropertySource(properties = "post.moderator.scheduler.moderateComment.cron=0/1 * * * * *")
class CommentModeratorTest {

    @Autowired
    private CommentModerator commentModerator;

    @MockBean
    private CommentService commentService;

    @Test
    void testModerateComment() throws InterruptedException {
        Thread.sleep(3000);
        verify(commentService, times(3)).moderateComment();
    }
}