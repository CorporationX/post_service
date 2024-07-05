package faang.school.postservice.service.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.corrector.GrammarBotProperties;
import faang.school.postservice.dto.corrector.Content;
import faang.school.postservice.dto.corrector.CorrectorResponse;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class PostCorrectorTest {
    @InjectMocks
    private PostCorrector corrector;
    @Mock
    private GrammarBotProperties properties;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private PostService postService;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private ResponseEntity<Object> response;

    private Post post;
    private CorrectorResponse expectedResponse;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        post = Post.builder()
                .content("Test content")
                .build();
        expectedResponse = new CorrectorResponse();
        expectedResponse.setCorrection("Corrected test content");

        String uri = "uri";

        when(properties.getKeyHeader()).thenReturn("keyHeader");
        when(properties.getKeyValue()).thenReturn("keyValue");
        when(properties.getContentTypeHeader()).thenReturn("contentTypeHeader");
        when(properties.getContentTypeValue()).thenReturn("contentTypeValue");
        when(properties.getSpellCheckerUri()).thenReturn(uri);

        when(response.getBody()).thenReturn("body");
        when(restTemplate.postForEntity(eq(uri), any(HttpEntity.class), any()))
                .thenReturn(response);
        when(objectMapper.readValue(anyString(), eq(CorrectorResponse.class))).thenReturn(expectedResponse);
    }


    @Test
    void correctPostsTest() {
        ArgumentCaptor<List<Post>> postsArgumentCaptor = ArgumentCaptor.forClass(List.class);
        when(postService.getAllDrafts()).thenReturn(List.of(post));

        corrector.correctPosts();

        verify(postService).updatePosts(postsArgumentCaptor.capture());
        assertEquals(expectedResponse.getCorrection(), postsArgumentCaptor.getValue().get(0).getContent());
    }

    @Test
    void getCorrectorResponse() {
        CorrectorResponse actualResponse = assertDoesNotThrow(() -> corrector.getCorrectorResponse(new Content()));

        assertEquals(expectedResponse, actualResponse);
    }
}