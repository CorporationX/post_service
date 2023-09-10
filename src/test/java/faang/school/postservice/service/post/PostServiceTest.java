package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    List<Post> listFindByVerifiedIsFalse;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(postService, "countOffensiveContentForBan", 1);

        Post post1 = Post.builder().authorId(1L).verified(false).build();
        Post post2 = Post.builder().authorId(1L).verified(false).build();
        Post post3 = Post.builder().authorId(3L).verified(true).build();
        Post post4 = Post.builder().authorId(2L).verified(false).build();
        Post post5 = Post.builder().authorId(2L).verified(false).build();

        listFindByVerifiedIsFalse = List.of(post1, post2, post3, post4, post5);
    }

    @Test
    void testGetByPostIsVerifiedFalse() {
        Mockito.when(postRepository.findByVerifiedIsFalse()).thenReturn(listFindByVerifiedIsFalse);
        List<Long> expected = List.of(1L, 2L);

        List<Long> actual = postService.getByPostIsVerifiedFalse();

        assertEquals(expected, actual);
    }

    @Test
    void testGetByPostIsVerifiedFalseWhenEmptyList() {
        Mockito.when(postRepository.findByVerifiedIsFalse()).thenReturn(List.of());
        List<Long> expected = List.of();

        List<Long> actual = postService.getByPostIsVerifiedFalse();

        assertEquals(expected, actual);
    }

    @Test
    void testGetByPostIsVerifiedFalseWhenNotVerified() {
        Mockito.when(postRepository.findByVerifiedIsFalse()).thenReturn(listFindByVerifiedIsFalse);
        List<Long> expected = List.of();
        ReflectionTestUtils.setField(postService, "countOffensiveContentForBan", 5);

        List<Long> actual = postService.getByPostIsVerifiedFalse();

        assertEquals(expected, actual);
    }
}