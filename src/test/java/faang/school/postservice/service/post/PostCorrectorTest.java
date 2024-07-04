package faang.school.postservice.service.post;

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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class PostCorrectorTest {
    @Spy
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

    @BeforeEach
    void setUp() {
        post = Post.builder()
                .content("Test content")
                .build();
    }


    @Test
    void correctPostsTest() throws IOException, InterruptedException {
        ArgumentCaptor<List<Post>> postsArgumentCaptor = ArgumentCaptor.forClass(List.class);
        when(postService.getAllDrafts()).thenReturn(List.of(post));
        CorrectorResponse correctorResponse = new CorrectorResponse();
        correctorResponse.setCorrection("Corrected test content");
        doReturn(correctorResponse).when(corrector).getCorrectorResponse(any(Content.class));

        corrector.correctPosts();

        verify(postService).updatePosts(postsArgumentCaptor.capture());
        assertEquals(correctorResponse.getCorrection(), postsArgumentCaptor.getValue().get(0).getContent());
    }

    @Test
    void getCorrectorResponse() throws IOException {
        String uri = "uri";

        when(properties.getKeyHeader()).thenReturn("keyHeader");
        when(properties.getKeyValue()).thenReturn("keyValue");
        when(properties.getContentTypeHeader()).thenReturn("contentTypeHeader");
        when(properties.getContentTypeValue()).thenReturn("contentTypeValue");
        when(properties.getSpellCheckerUri()).thenReturn(uri);

        when(response.getBody()).thenReturn("body");
        when(restTemplate.postForEntity(eq(uri), any(HttpEntity.class), any()))
                .thenReturn(response);


        assertDoesNotThrow(() -> corrector.getCorrectorResponse(new Content()));


        verify(objectMapper).readValue(anyString(), eq(CorrectorResponse.class));
    }
}