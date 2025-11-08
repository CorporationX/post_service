package faang.school.postservice.service.comment;


import faang.school.postservice.messages.redis.publishers.Publisher;
import faang.school.postservice.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.listener.ChannelTopic;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class UserBanServiceImplTest {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private Publisher publisherBanUsers;

    @Mock
    private ChannelTopic userBanTopic;

    @InjectMocks
    private UserBanServiceImpl userBanService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void banUserComment_withNonEmptyList_shouldPublishMessage() {
        List<Long> fakeList = List.of(1L, 2L, 3L);
        when(commentRepository.findAllUsersForBan(anyInt())).thenReturn(fakeList);
        when(userBanTopic.getTopic()).thenReturn("userBanTopic");

        userBanService.banUserComment();

        verify(publisherBanUsers, times(1)).publish(eq(userBanTopic), eq(fakeList));
    }

    @Test
    void banUserComment_withEmptyList_shouldNotPublish() {
        when(commentRepository.findAllUsersForBan(anyInt())).thenReturn(List.of());

        userBanService.banUserComment();

        verifyNoInteractions(publisherBanUsers);
    }
}