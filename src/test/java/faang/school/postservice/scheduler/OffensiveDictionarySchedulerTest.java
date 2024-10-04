package faang.school.postservice.scheduler;

import faang.school.postservice.service.dictionary.OffensiveDictionaryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OffensiveDictionarySchedulerTest {

    @InjectMocks
    private OffensiveDictionaryScheduler offensiveDictionaryScheduler;

    @Mock
    private OffensiveDictionaryService offensiveDictionaryService;

    @Test
    @DisplayName("Should call update offensive dictionary method")
    void whenInitThenShouldCallModerateCommentsContent() {
        offensiveDictionaryScheduler.updateOffensiveDictionary();

        verify(offensiveDictionaryService).updateOffensiveDictionary();
    }
}