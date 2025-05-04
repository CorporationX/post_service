package faang.school.postservice.scheduler;

import faang.school.postservice.service.userModeration.UserModerationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class AuthorBannerTest {
    @Mock
    private UserModerationService userModerationService;

    @InjectMocks
    private AuthorBanner authorBanner;

    @Test
    void test_runModerationTask(){
        authorBanner.runModerationTask();

        verify(userModerationService, times(1)).checkAndBanUsersWithUnverifiedPosts();
    }
}