package faang.school.postservice.service;

import faang.school.postservice.config.SpellCheckerConfig;
import faang.school.postservice.dto.spell.CorrectedResponse;
import faang.school.postservice.dto.spell.SpellCheckerResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpellCheckerServiceTest {

    private SpellCheckerService spellCheckerService;

    @Mock
    private RestTemplate restTemplate;

    private String messageToCheck;
    @Captor
    private ArgumentCaptor<URI> argumentCaptor;

    @BeforeEach
    public void setUp() {
        String url = "https://api.textgears.com/correct";
        String textParam = "textParam";
        String keyParam = "keyParam";
        String key = "key";
        String languageParam = "languageParam";
        String language = "language";
        messageToCheck = "Message To Check";
        SpellCheckerConfig spellCheckerConfig =
                new SpellCheckerConfig(url, textParam, keyParam, key, languageParam, language);
        spellCheckerService = new SpellCheckerService(restTemplate, spellCheckerConfig);
    }

    @Test
    @DisplayName("testing checkMessage with null responseDto")
    void testCheckMessageWithNullResponseDto() {
        SpellCheckerResponseDto responseDto = new SpellCheckerResponseDto();
        when(restTemplate.getForObject(argumentCaptor.capture(), eq(SpellCheckerResponseDto.class)))
                .thenReturn(responseDto);
        Optional<String> result = spellCheckerService.checkMessage(messageToCheck);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("testing checkMessage with null responseDto")
    void testCheckMessageWithTrueStatusResponseDto() {
        SpellCheckerResponseDto responseDto = SpellCheckerResponseDto.builder()
                .status(true)
                .response(new CorrectedResponse("correct text")).build();
        when(restTemplate.getForObject(argumentCaptor.capture(), eq(SpellCheckerResponseDto.class)))
                .thenReturn(responseDto);
        Optional<String> result = spellCheckerService.checkMessage(messageToCheck);
        assertTrue(result.isPresent());
    }
}