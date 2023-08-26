package faang.school.postservice.service.postCorrecter;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BingSpellServiceTest {
    @Mock
    private PostCorrecter postCorrecter;
    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private BingSpellService spellService;

    private List<Post> posts;

    @BeforeEach
    public void setup() {
        posts = List.of(
                Post.builder().id(1L).content("content1").build(),
                Post.builder().id(2L).content("content2").build(),
                Post.builder().id(3L).content("content3").build()
        );
        Mockito.when(postRepository.findReadyToPublish()).thenReturn(posts);
        for (Post post : posts) {
            Mockito.when(postCorrecter.correctPostText(post))
                    .thenReturn(CompletableFuture.completedFuture(post.getContent() + "!"));
        }
        Mockito.when(postRepository.save(Mockito.any())).thenReturn(posts.get(0));
    }

    @Test
    public void testCorrectPosts() {
        spellService.correctUnpublishedPosts();
        Mockito.verify(postRepository).findReadyToPublish();
        for (Post post : posts) {
            Mockito.verify(postCorrecter).correctPostText(post);
            post.setContent(post.getContent() + "!");
            Mockito.verify(postRepository).save(post);
        }
    }

    @Test
    public void testIncorrectPosts() {
        Mockito.when(postCorrecter.correctPostText(posts.get(0)))
                .thenThrow(new RuntimeException());

        spellService.correctUnpublishedPosts();
        Mockito.verify(postRepository).findReadyToPublish();
        Mockito.verify(postCorrecter).correctPostText(posts.get(0));
        for (int i = 1; i < posts.size(); i++) {
            Mockito.verify(postCorrecter).correctPostText(posts.get(i));
            posts.get(i).setContent(posts.get(i).getContent() + "!");
            Mockito.verify(postRepository).save(posts.get(i));
        }
        Mockito.verify(postRepository, Mockito.times(0)).save(posts.get(0));
    }
}