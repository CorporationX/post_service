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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        PostDto postDto = postService.createPostDraft(postDraftDto);
        verify(postRepository, times(1)).save(Mockito.any(Post.class));
        Assertions.assertNotNull(postDto);
    }

    @Test
    public void testGetAllPostsByAuthorId_success() {
        when(postRepository.findByAuthorId(1L)).thenReturn(getPostsWithAuthorId_1_success());
        List<PostDto> postDtos = postService.getAllPostsByAuthorId(1L);
        verify(postRepository, times(1)).findByAuthorId(1L);
        Assertions.assertEquals(2, postDtos.size());
        Assertions.assertEquals(2, postDtos.stream()
                .filter(p -> p.getAuthorId() == 1L)
                .count());
        Assertions.assertEquals(4, postDtos.get(0).getId());
    }

    @Test
    public void testPublishPost_success() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.

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
