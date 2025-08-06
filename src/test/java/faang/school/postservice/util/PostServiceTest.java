package faang.school.postservice.util;

import faang.school.postservice.dto.post.PostDraftDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.integration.project.service.ProjectClient;
import faang.school.postservice.integration.user.service.UserClient;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.impl.PostServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private UserClient userClient;

    @Mock
    private ProjectClient projectClient;

    @Mock
    private PostRepository postRepository;

    @Spy
    private PostMapperImpl postMapper;

    @InjectMocks
    private PostServiceImpl postService;

    @BeforeEach
    public void setUp() {
        postService = new PostServiceImpl(postMapper, postRepository, projectClient, userClient);
    }

    @Test
    public void testFindById_success() {
        when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(createPost()));
        PostDto post = postService.findById(1L);
        verify(postRepository, times(1)).findById(Mockito.anyLong());
        Assertions.assertNotNull(post);
    }

    @Test
    public void testFindById_notFound() {
        Assertions.assertThrows(EntityNotFoundException.class, () -> postService.findById(1L));
    }

    @Test
    public void testMarkPostAsDeleted_success() {
        when(postRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(createPost()));
        Post deletedPost = createPost();
        deletedPost.setDeleted(true);
        deletedPost.setPublished(false);
        when(postRepository.save(Mockito.any(Post.class))).thenReturn(deletedPost);
        PostDto postDto = postService.markPostAsDeleted(1L);
        verify(postRepository, times(1)).findById(Mockito.anyLong());
        verify(postRepository, times(1)).save(Mockito.any(Post.class));
        Assertions.assertTrue(postDto.isDeleted());
        Assertions.assertFalse(postDto.isPublished());
    }

    @Test
    public void testMarkPostAsDeleted_notFound() {
        when(postRepository.findById(Mockito.anyLong())).thenReturn(null);
        Assertions.assertThrows(EntityNotFoundException.class, () -> postService.markPostAsDeleted(1L));
    }

    @Test
    public void testCreatePostDraft_success() {
        PostDraftDto postDraftDto = createPostDraftDto();
//        when(postService.createPostDraft(postDraftDto)).thenReturn(createPostDto());
        PostDto postDto = postService.createPostDraft(postDraftDto);
        verify(postRepository, times(1)).save(Mockito.any(Post.class));
        Assertions.assertNotNull(postDto);
    }


    private Post createPost() {
        return new Post(1L, "abc", 1L, null, List.of(), List.of(), List.of(),
                null, List.of(), true, LocalDateTime.now(), LocalDateTime.now(), false,
                LocalDateTime.now(), LocalDateTime.now());
    }

    private PostDto createPostDto() {
        return new PostDto("cde", 1L, null, 1L, true, true, LocalDateTime.now());
    }

    private PostDraftDto createPostDraftDto() {
        return new PostDraftDto("cde", 1L, null);
    }
}
