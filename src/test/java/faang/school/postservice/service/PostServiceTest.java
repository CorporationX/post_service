package faang.school.postservice.service;

import faang.school.postservice.config.ModerationProperties;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    private static final Long POST_ID_1 = 1L;
    private static final Long POST_ID_2 = 2L;

    private static final String ORIGINAL_TEXT_1 = "Ths is a tst post";
    private static final String ORIGINAL_TEXT_2 = "Anther exmple with errrs";

    private static final String CORRECTED_TEXT_1 = "This is a test post";
    private static final String CORRECTED_TEXT_2 = "Another example with errors";

    private static final String API_ERROR_MESSAGE = "Error connecting to GrammarBot API";

    @Mock
    private PostRepository postRepository;

    @Mock
    private GrammarBotService grammarBotService;

    @Mock
    private ModerationProperties moderationProperties;

    @InjectMocks
    private PostService postService;

    private Post post1;
    private Post post2;

    @BeforeEach
    void setUp() {
        post1 = new Post();
        post1.setId(POST_ID_1);
        post1.setContent(ORIGINAL_TEXT_1);

        post2 = new Post();
        post2.setId(POST_ID_2);
        post2.setContent(ORIGINAL_TEXT_2);
        moderationProperties.setBatchSize(1);
    }

    @Test

    public void correctUnpublishedPosts_ShouldCorrectGrammarAndSavePosts() {
        when(postRepository.findReadyToPublish()).thenReturn(List.of(post1, post2));
        when(grammarBotService.checkGrammar(ORIGINAL_TEXT_1)).thenReturn(CORRECTED_TEXT_1);
        when(grammarBotService.checkGrammar(ORIGINAL_TEXT_2 )).thenReturn(CORRECTED_TEXT_2);
        when(moderationProperties.getBatchSize()).thenReturn(1);
        postService.correctUnpublishedPosts();

        when(postRepository.findByVerifiedAtIsNull()).thenReturn(Collections.emptyList());
        verify(postRepository, times(1)).findReadyToPublish();
        verify(grammarBotService, times(1)).checkGrammar(ORIGINAL_TEXT_1);
        verify(grammarBotService, times(1)).checkGrammar(ORIGINAL_TEXT_2);
        verify(postRepository, times(1)).save(argThat(post ->
                post.getId().equals(POST_ID_1) && post.getContent().equals(CORRECTED_TEXT_1)));
        verify(postRepository, times(1)).save(argThat(post ->
                post.getId().equals(POST_ID_2) && post.getContent().equals(CORRECTED_TEXT_2)));
        postService.moderateAllUnverifiedPosts();
    }

    @Test
    void correctUnpublishedPosts_ShouldContinueProcessingEvenIfGrammarServiceFails() {
        when(postRepository.findReadyToPublish()).thenReturn(List.of(post1, post2));
        when(grammarBotService.checkGrammar(ORIGINAL_TEXT_1)).thenReturn(CORRECTED_TEXT_1);
        when(grammarBotService.checkGrammar(ORIGINAL_TEXT_2))
                .thenThrow(new RuntimeException(API_ERROR_MESSAGE));

        verify(postRepository, never()).saveAll(any());
        postService.correctUnpublishedPosts();

        verify(grammarBotService, times(1)).checkGrammar(ORIGINAL_TEXT_1);
        verify(grammarBotService, times(1)).checkGrammar(ORIGINAL_TEXT_2);
        verify(postRepository, times(1)).save(argThat(post ->
                post.getId().equals(POST_ID_1) && post.getContent().equals(CORRECTED_TEXT_1)));
        verify(postRepository, never()).save(argThat(post ->
                post.getId().equals(POST_ID_2)));
    }
}
