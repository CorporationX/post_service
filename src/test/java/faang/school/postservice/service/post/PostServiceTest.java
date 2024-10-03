package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    private static final long ID = 1L;
    private Post post;

    @BeforeEach
    void init() {
        post = Post.builder()
                .id(ID)
                .build();
    }

    @Nested
    class PostServiceTests {

        @Test
        @DisplayName("When Post ID is valid then return the Post")
        void whenFindByIdThenSuccess() {
            when(postRepository.findById(ID)).thenReturn(Optional.of(post));

            postService.findById(ID);

            assertEquals(1L, ID);
        }

        @Test
        @DisplayName("When Post ID is invalid then throw EntityNotFoundException")
        void whenFindByIdThenThrowException() {
            when(postRepository.findById(ID)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,() -> postService.findById(ID));
        }
    }
}
