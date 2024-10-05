package faang.school.postservice.service.impl;

import faang.school.postservice.client.TextGearsClient;
import faang.school.postservice.dto.post.ProgressPost;
import faang.school.postservice.dto.text.gears.TextGearsResponse;
import faang.school.postservice.exception.TextGearsException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.TextGearsValidatorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class ContentCorrecterServiceImplTest {

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private TextGearsClient correcterTextClient;

    @MockBean
    private TextGearsValidatorImpl textGearsValidator;

    @SpyBean
    private PostMapper postMapper;

    @Autowired
    private ContentCorrecterServiceImpl contentCorrecterService;

    @Value("${post.correct.content.retry.max-attempts}")
    private int maxAttempts;

    private final String originalContent = "content";
    private ProgressPost post;
    private List<ProgressPost> posts;

    @BeforeEach
    void setUp() {
        post = new ProgressPost(1L, originalContent);
        posts = List.of(post);
    }

    @Test
    void testCorrectAllPosts_NoPosts() {
        when(postRepository.findNotPublishedAndNotDeletedPosts()).thenReturn(Collections.emptyList());

        contentCorrecterService.correctAllPosts();

        verify(textGearsValidator, never()).isCorrectResponse(any(TextGearsResponse.class));
        verify(correcterTextClient, never()).correctText(anyString());
        verify(postRepository).saveAll(anyList());
    }

    @Test
    void testCorrectAllPosts_WithPosts() {
        String correctedContent = "corrected content";
        TextGearsResponse response = TextGearsResponse.builder()
                .response(new TextGearsResponse.Response(correctedContent))
                .build();

        when(postRepository.findNotPublishedAndNotDeletedPosts()).thenReturn(posts);
        when(correcterTextClient.correctText(originalContent)).thenReturn(response);

        contentCorrecterService.correctAllPosts();

        verify(correcterTextClient).correctText(originalContent);
        verify(textGearsValidator).isCorrectResponse(response);
        verify(postMapper).toEntity(posts);
        verify(postRepository).saveAll(anyList());
    }

    @Test
    void testCorrectContent_RetriesOnException() {
        String errorMessage = "Error message";
        TextGearsResponse response = TextGearsResponse.builder()
                .response(new TextGearsResponse.Response(originalContent))
                .build();
        when(postRepository.findNotPublishedAndNotDeletedPosts()).thenReturn(posts);
        when(correcterTextClient.correctText(originalContent)).thenReturn(response);
        doThrow(new TextGearsException(errorMessage)).when(textGearsValidator).isCorrectResponse(any());

        assertDoesNotThrow(() -> contentCorrecterService.correctAllPosts());

        verify(correcterTextClient, times(maxAttempts)).correctText(originalContent);
        verify(postMapper).toEntity(posts);
        verify(postRepository).saveAll(anyList());
        assertEquals(originalContent, post.getContent());
    }
}
