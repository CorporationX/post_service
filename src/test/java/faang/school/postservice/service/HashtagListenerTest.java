package faang.school.postservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.hashtag.HashtagDto;
import faang.school.postservice.dto.post.PostDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.connection.Message;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class HashtagListenerTest {
    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HashtagService hashtagService;

    @InjectMocks
    private HashtagListener<PostDto> hashtagListener;

    private Message message;
    private PostDto postDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        postDto = PostDto.builder()
                .id(1L)
                .content("This is a test content with #hashtag1 and #hashtag2")
                .build();

        message = mock(Message.class);
        when(message.getBody()).thenReturn("Test message body".getBytes());
    }

    @Test
    void shouldHandleMessageAndSaveHashtagsSuccessfully() throws Exception {
        when(objectMapper.readValue(any(byte[].class), eq(PostDto.class))).thenReturn(postDto);

        Set<HashtagDto> hashtags = Set.of(
                HashtagDto.builder().postId(1L).content("#hashtag1").build(),
                HashtagDto.builder().postId(1L).content("#hashtag2").build()
        );
        doNothing().when(hashtagService).saveHashtags(any(Set.class));

        hashtagListener.onMessage(message, null);

        verify(hashtagService, times(1)).saveHashtags(hashtags);
    }

    @Test
    void shouldFindHashtagsSuccessfully() {
        Set<HashtagDto> hashtags = hashtagListener.findHashtags("This is a #test with multiple #hashtags #test", 1L);

        assertEquals(2, hashtags.size());
        assertTrue(hashtags.stream().anyMatch(hashtag -> hashtag.getContent().equals("#test")));
        assertTrue(hashtags.stream().anyMatch(hashtag -> hashtag.getContent().equals("#hashtags")));
    }

}