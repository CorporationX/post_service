package faang.school.postservice.client;

import faang.school.postservice.dto.commentAnalyzer.request.CommentRequestDto;
import faang.school.postservice.dto.commentAnalyzer.response.ToxicityScoreDto;
import faang.school.postservice.exception.CommentAnalyzerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentAnalyzerTest {
    @InjectMocks
    private CommentAnalyzer commentAnalyzer;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    public void setUp() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri((Function<UriBuilder, URI>) any())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any(CommentRequestDto.class)))
                .thenAnswer(inv -> requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    public void testAnalyzeComment_success() {
        String text = "just a text";
        ToxicityScoreDto response = new ToxicityScoreDto();

        when(responseSpec.bodyToMono(ToxicityScoreDto.class)).thenReturn(Mono.just(response));

        ToxicityScoreDto result = commentAnalyzer.analyzeComment(text).block();
        assertNotNull(result);
        assertEquals(response, result);

        verify(webClient, times(1)).post();
        verify(requestBodyUriSpec).uri((Function<UriBuilder, URI>) any());
    }

    @Test
    public void testAnalyzeComment_ApiError() {
        String text = "just a text";

        when(responseSpec.bodyToMono(ToxicityScoreDto.class))
                .thenReturn(Mono.error(new CommentAnalyzerException(
                        "Comment analyzer API error", HttpStatus.BAD_REQUEST)));

        CommentAnalyzerException result = assertThrows(CommentAnalyzerException.class,
                () -> commentAnalyzer.analyzeComment(text).block()
        );

        assertEquals("Comment analyzer API error (HTTP 400 BAD_REQUEST)", result.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }
}
