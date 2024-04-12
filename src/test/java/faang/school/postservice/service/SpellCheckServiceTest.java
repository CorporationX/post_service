package faang.school.postservice.service;

import faang.school.postservice.service.post.PostService;
import faang.school.postservice.service.post.SpellCheckService;
import faang.school.postservice.dto.corrector.CorrectWordDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SpellCheckServiceTest {
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private PostService postService;
    @Mock
    private PostRepository postRepository;
    @InjectMocks
    private SpellCheckService spellCheckService;

    @Test
    public void test_spellCheckTextInPosts_Successful() {
        List<Post> posts = getPosts();
        String yandexUrl = "https://speller.yandex.net/services/spellservice.json/checkText";
        String finalUrlFirstPost = yandexUrl + "?text=" + posts.get(0).getContent();
        String finalUrlSecondPost = yandexUrl + "?text=" + posts.get(1).getContent();
        ReflectionTestUtils.setField(spellCheckService, "url", yandexUrl);

        when(postService.findReadyToPublishAndUncorrected()).thenReturn(posts);
        when(restTemplate.getForObject(finalUrlFirstPost, CorrectWordDto[].class)).thenReturn(new CorrectWordDto[]{});
        when(restTemplate.getForObject(finalUrlSecondPost, CorrectWordDto[].class)).thenReturn(new CorrectWordDto[]{});

        Assertions.assertDoesNotThrow(() -> spellCheckService.spellCheckTextInPosts());
        verify(postService).findReadyToPublishAndUncorrected();
        verify(restTemplate, times(2)).getForObject(anyString(), any(Class.class));
    }

    @Test
    public void test_spellCheckPostById_Failed() {
        Long postId = 1L;

        when(postService.findPostById(postId)).thenThrow(EntityNotFoundException.class);

        Assertions.assertThrows(EntityNotFoundException.class, () -> spellCheckService.spellCheckPostById(postId));
    }

    @Test
    public void test_spellCheckPostById_Successful() {
        Long postId = 1L;
        Post post = Post.builder().id(postId).content("Привет").build();
        String yandexUrl = "https://speller.yandex.net/services/spellservice.json/checkText";
        String finalUrlPost = yandexUrl + "?text=" + post.getContent();
        ReflectionTestUtils.setField(spellCheckService, "url", yandexUrl);

        when(postService.findPostById(postId)).thenReturn(post);
        when(restTemplate.getForObject(finalUrlPost, CorrectWordDto[].class)).thenReturn(new CorrectWordDto[]{});

        Assertions.assertDoesNotThrow(() -> spellCheckService.spellCheckPostById(postId));
        verify(postService).findPostById(postId);
        verify(restTemplate, times(1)).getForObject(anyString(), any(Class.class));
    }

    private List<Post> getPosts() {
        return new ArrayList<>(List.of(
                Post.builder().content("Привет!").build(),
                Post.builder().content("Тебе тоже привет!").build()));
    }
}
