package faang.school.postservice.publisher.user_ban;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class UserBanEventPublisherTest {

    @Mock
    private UserBanEventPublisher userBanEventPublisher;

    @Mock
    private ObjectMapper objectMapper;

    @Test
    void testPublishEvent_should
}
