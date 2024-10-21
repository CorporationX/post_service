package faang.school.postservice.service.dictionary;

import faang.school.postservice.client.DictionaryClient;
import faang.school.postservice.config.dictionary.OffensiveWordsDictionary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class OffensiveDictionaryServiceTest {

    private static final int TWO_TIMES_USES = 2;

    @Mock
    private DictionaryClient dictionaryClient;

    @Mock
    private OffensiveWordsDictionary offensiveWordsDictionary;

    private OffensiveDictionaryService offensiveDictionaryService;
    private ExecutorService executorService = Executors.newCachedThreadPool();

    @BeforeEach
    void before() {
        offensiveDictionaryService = new OffensiveDictionaryService(dictionaryClient,
                executorService, offensiveWordsDictionary);
    }

    @Test
    @DisplayName("Should run two methods async")
    void whenFeignRuAndEngReturnSmthThenUpdateDictionary() {
        byte[] bytes = {98, 121, 99, 104, 97, 114, 97};

        when(dictionaryClient.getRuWords())
                .thenReturn(ResponseEntity.ok(bytes));
        when(dictionaryClient.getEngWords())
                .thenReturn(ResponseEntity.ok(bytes));

        offensiveDictionaryService.updateOffensiveDictionary().join();

        verify(offensiveWordsDictionary, times(TWO_TIMES_USES))
                .addWordsToDictionary(any());

    }
}
