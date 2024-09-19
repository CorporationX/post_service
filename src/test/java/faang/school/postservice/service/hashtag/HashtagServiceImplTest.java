package faang.school.postservice.service.hashtag;

import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.hashtag.HashtagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class HashtagServiceImplTest {

    @Mock
    private HashtagRepository hashtagRepository;

    @Captor
    ArgumentCaptor<List<Hashtag>> hashtagListCaptor;

    @InjectMocks
    private HashtagServiceImpl hashtagService;

    @Test
    void testCreateHashtags() {
        Post post = Post.builder().content("sdgdgdfg #123").build();

        hashtagService.createHashtags(post);

        verify(hashtagRepository).saveAll(hashtagListCaptor.capture());

        assertEquals("#123", hashtagListCaptor.getValue().get(0).getName());
    }

    @Test
    void testCreateHashtagsEmptyContentOk() {
        Post post = Post.builder().build();

        hashtagService.createHashtags(post);

        verify(hashtagRepository).saveAll(any());
    }

    @Test
    void testUpdateHashtagsOk() {
        Post post = Post.builder()
                .content("#another asd #hash")
                .hashtags(new ArrayList<>(List.of(Hashtag.builder().name("#pups").build())))
                .build();

        hashtagService.updateHashtags(post);

        assertEquals(2, post.getHashtags().size());
    }

    @Test
    void findPostsByHashtagOk() {
        when(hashtagRepository.findByName(anyString()))
                .thenReturn(Optional.of(Hashtag.builder().name("#rere").posts(List.of(Post.builder().build())).build()));

        assertEquals(1, hashtagService.findPostsByHashtag("asd").size());
    }
}
