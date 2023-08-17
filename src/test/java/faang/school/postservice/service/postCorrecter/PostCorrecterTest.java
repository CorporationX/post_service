package faang.school.postservice.service.postCorrecter;

import faang.school.postservice.client.BingSpellClient;
import faang.school.postservice.config.postCorrecter.BingSpellConfig;
import faang.school.postservice.dto.postCorrecter.FlaggedTokenDto;
import faang.school.postservice.dto.postCorrecter.SpellCheckDto;
import faang.school.postservice.dto.postCorrecter.SuggestionDto;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PostCorrecterTest {
    @Mock
    private BingSpellConfig bingSpellConfig;
    @Mock
    private BingSpellClient bingSpellClient;
    @InjectMocks
    private PostCorrecter postCorrecter;

    @BeforeEach
    public void setup() {
        SpellCheckDto spellCheckDto = SpellCheckDto.builder()
                ._type("type")
                .correctionType("type")
                .flaggedTokens(getDtos())
                .build();
        Mockito.when(bingSpellConfig.getHeaders())
                .thenReturn(Collections.emptyMap());
        Mockito.when(bingSpellConfig.getMode())
                .thenReturn("spell");
        Mockito.when(bingSpellClient.checkSpell(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(ResponseEntity.of(Optional.of(spellCheckDto)));
    }

    @Test
    public void testCorrectingPost() throws ExecutionException, InterruptedException {
        Post post = Post.builder()
                .id(1L)
                .content("heello, Bob. I was in cafe yesterdat")
                .build();
        var result = postCorrecter.correctPostText(post);
        post.setContent("hello, Bob. I was in cafe yesterday");
        assertEquals(CompletableFuture.completedFuture(post).get().getContent(), result.get());
    }

    private List<FlaggedTokenDto> getDtos() {
        SuggestionDto suggestionDto = SuggestionDto.builder()
                .suggestion("yesterday")
                .build();
        FlaggedTokenDto flaggedTokenDto = FlaggedTokenDto.builder()
                .token("yesterdat")
                .offset(10L)
                .suggestions(List.of(suggestionDto))
                .build();
        SuggestionDto suggestionDto1 = SuggestionDto.builder()
                .suggestion("hello")
                .build();
        FlaggedTokenDto flaggedTokenDto1 = FlaggedTokenDto.builder()
                .token("heello")
                .offset(10L)
                .suggestions(List.of(suggestionDto1))
                .build();
        return List.of(flaggedTokenDto1, flaggedTokenDto);
    }
}