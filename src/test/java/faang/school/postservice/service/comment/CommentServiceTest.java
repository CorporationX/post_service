package faang.school.postservice.service.comment;

import faang.school.postservice.repository.CommentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = "commenter-banner.comments-count-for-ban=5")
public class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Test
    public void testFindAuthorIdsForBan_shouldReturnValidData() {
        List<Long> expectedResult = List.of(1L, 2L, 3L);

        when(commentRepository.findAuthorsForBanWithUnverifiedCommentsCount(anyInt()))
                .thenReturn(expectedResult);

        List<Long> actualResult = commentService.findAuthorIdsForBan();

        Assertions.assertEquals(expectedResult, actualResult);
    }
}
