package faang.school.postservice.service;

import faang.school.postservice.service.grammar.GrammarBot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrammarBotTest {

    @InjectMocks
    private GrammarBot grammarBot;

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(grammarBot, "apiUrl", "https://grammarbot.p.rapidapi.com/check");
        ReflectionTestUtils.setField(grammarBot, "apiKey", "test-api-key");
        ReflectionTestUtils.setField(grammarBot, "apiHost", "grammarbot.p.rapidapi.com");
    }

    @Test
    void checkGrammar_shouldReturnCorrectedText_whenApiReturnsValidResponse() throws Exception {

        String mockedResponse = "{\"result\":\"Corrected Text\"}";
        when(httpResponse.body()).thenReturn(mockedResponse);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);

        String result = grammarBot.checkGrammar("I loves programming");

        assertEquals(mockedResponse, result);

        verify(httpClient, times(1)).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    void checkGrammar_shouldThrowException_whenApiCallFails() throws Exception {

        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new RuntimeException("API failure"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            grammarBot.checkGrammar("This is a test");
        });

        assertEquals("Failed to call GrammarBot API", exception.getMessage());
    }
}

