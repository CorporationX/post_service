package faang.school.postservice.scheduler;

import faang.school.postservice.service.spellcheck.PostCorrectionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PostCorrecterTest {

    @Mock
    private PostCorrectionService correctionService;

    @InjectMocks
    private PostCorrecter postCorrecter;

    @Test
    @DisplayName("Should call correctionService when job runs")
    void shouldRunCorrectionJob() {
        postCorrecter.runCorrectionJob();

        verify(correctionService).correctAllUnpublishedPosts();
    }
}
