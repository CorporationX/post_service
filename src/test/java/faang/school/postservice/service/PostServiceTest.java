package faang.school.postservice.service;

import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private PostRepository postRepository;
    @InjectMocks
    private PostService postService;

    @Test
    public void testGetAuthorsWithExcessVerifiedFalsePostsSuccess() {
        List<Long> expectedAuthorsIds = List.of(1L, 2L, 3L);

        when(postRepository.findAuthorsWithExcessVerifiedFalsePosts()).thenReturn(expectedAuthorsIds);

        List<Long> actualAuthorsIds = postService.getAuthorsWithExcessVerifiedFalsePosts();

        assertEquals(expectedAuthorsIds, actualAuthorsIds);
        verify(postRepository, atLeastOnce()).findAuthorsWithExcessVerifiedFalsePosts();
    }
}
