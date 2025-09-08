package faang.school.postservice.util;

import faang.school.postservice.dto.post.PostDraftDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.integration.project.dto.ProjectResponseDto;
import faang.school.postservice.integration.project.service.ProjectClient;
import faang.school.postservice.integration.user.dto.UserResponseDto;
import faang.school.postservice.integration.user.service.UserClient;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.impl.PostServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import liquibase.hub.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
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
        postService = new PostServiceImpl(postMapper, postRepository, projectClient, userClient, null);
    }

    @Test
    public void testFindById_success() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(createPost()));
        PostDto post = postService.findById(1L);
        verify(postRepository, times(1)).findById(anyLong());
        assertNotNull(post);
    }

    @Test
    public void testFindById_notFound() {
        assertThrows(EntityNotFoundException.class, () -> postService.findById(1L));
    }

    @Test
    public void testMarkPostAsDeleted_success() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(createPost()));
        Post deletedPost = createPost();
        deletedPost.setDeleted(true);
        deletedPost.setPublished(false);
        when(postRepository.save(any(Post.class))).thenReturn(deletedPost);
        PostDto postDto = postService.markPostAsDeleted(1L);
        verify(postRepository, times(1)).findById(anyLong());
        verify(postRepository, times(1)).save(any(Post.class));
        assertTrue(postDto.isDeleted());
        assertFalse(postDto.isPublished());
    }

    @Test
    public void testGetAllPostsByAuthorId_success() {
        when(postRepository.findByAuthorId(1L)).thenReturn(getPostsWithAuthorId_1_success());
        List<PostDto> postDtos = postService.getAllPostsByAuthorId(1L);
        verify(postRepository, times(1)).findByAuthorId(1L);
        assertEquals(2, postDtos.size());
        assertEquals(2, postDtos.stream()
                .filter(p -> p.getAuthorId() == 1L)
                .count());
        assertEquals(4, postDtos.get(0).getId());
    }

    private Post createPost() {
        return new Post(1L, "abc", 1L, null, List.of(), List.of(), List.of(),
                null, List.of(), true, LocalDateTime.now(), LocalDateTime.now(), false,
                LocalDateTime.now(), LocalDateTime.now());
    }

    private PostDraftDto createPostDraftDto() {
        return new PostDraftDto("cde", 1L, null);
    }

    private List<Post> getPostsWithAuthorId_1_success() {
        List<Post> posts = new ArrayList<>(List.of(
                new Post(5L, "frt", 1L, null, List.of(), List.of(), List.of(),
                        null, List.of(), false, LocalDateTime.now(), LocalDateTime.now(), false,
                        LocalDateTime.now().minusDays(3), LocalDateTime.now()),
                new Post(4L, "frt", 1L, null, List.of(), List.of(), List.of(),
                        null, List.of(), false, LocalDateTime.now(), LocalDateTime.now(), false,
                        LocalDateTime.now(), LocalDateTime.now())
        ));
        return posts;
    }

    private URL getResource(String fileName) {
        return this.getClass().getClassLoader().getResource(fileName);
    }
}
