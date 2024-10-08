package faang.school.postservice.service.dictionary;

import faang.school.postservice.client.DictionaryClient;
import faang.school.postservice.config.dictionary.OffensiveWordsDictionary;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutorService;


@ExtendWith(MockitoExtension.class)
class OffensiveDictionaryServiceTest {

    @InjectMocks
    private OffensiveDictionaryService offensiveDictionaryService;

    @Mock
    private DictionaryClient dictionaryClient;

    @Mock
    private ExecutorService executorService;

    @Mock
    private OffensiveWordsDictionary offensiveWordsDictionary;

    private byte[] bytes = {98, 121, 99, 104, 97, 114, 97};

/*    @Test
    @DisplayName("Should run two methods async")
    void whenFeignRuAndEngReturnSmthThenUpdateDictionary() {
        when(dictionaryClient.getRuWords())
                .thenReturn(ResponseEntity.ok(bytes));
        when(dictionaryClient.getEngWords())
                .thenReturn(ResponseEntity.ok(bytes));

        offensiveDictionaryService.updateOffensiveDictionary();

        verify(offensiveWordsDictionary, times(2))
                .addWordsToDictionary(any());

    }*/
}
