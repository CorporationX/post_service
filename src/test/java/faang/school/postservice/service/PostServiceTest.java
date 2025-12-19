package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @InjectMocks
    private PostService postService;
    @Mock
    private PostRepository postRepository;
    @Spy
    private UserServiceClient userServiceClient;
    @Spy
    private ProjectServiceClient projectServiceClient;
    @Spy
    private PostMapperImpl postMapper;

    @Test
    void testCreatingPostForAuthor_NullPointerException() {
        PostDto post = new PostDto();
        post.setAuthorId(20L);

        doThrow(new NullPointerException()).when(userServiceClient).getUser(anyLong());

        assertThrows(NullPointerException.class, () -> postService.createPost(post));
    }

    @Test
    void testCreatingPostForAuthor_RuntimeException() {
        PostDto post = new PostDto();
        post.setAuthorId(1L);

        doThrow(new RuntimeException("Test runtime exception")).when(userServiceClient).getUser(anyLong());

        assertThrows(RuntimeException.class, () -> postService.createPost(post));
    }

    @Test
    void testCreatingPostForProject_NullPointerException() {
        PostDto post = new PostDto();
        post.setProjectId(20L);

        doThrow(new NullPointerException ()).when(projectServiceClient).getProject(anyLong());

        assertThrows(NullPointerException.class, () -> postService.createPost(post));
    }

    @Test
    void testCreatingPostForProject_RuntimeException() {
        PostDto post = new PostDto();
        post.setProjectId(1L);

        doThrow(new RuntimeException("Test runtime exception")).when(projectServiceClient).getProject(anyLong());

        assertThrows(RuntimeException.class, () -> postService.createPost(post));
    }

    @Test
    void testCreatingPostForAuthor() {
        PostDto postDto = new PostDto();
        postDto.setContent("assd");
        postDto.setAuthorId(1L);
        UserDto userDto = new UserDto(1L, "Petr", "mail");
        Post post = postMapper.toPost(postDto);

        when(postMapper.toPost(postDto)).thenReturn(post);
        when(userServiceClient.getUser(postDto.getAuthorId())).thenReturn(userDto);

        postService.createPost(postDto);

        verify(postRepository, times(1)).save(post);

    }

    @Test
    void testCreatingPostForProject() {
        PostDto postDto = new PostDto();
        postDto.setContent("assd");
        postDto.setProjectId(1L);
        ProjectDto projectDto = new ProjectDto(1L, "Petr_Project");
        Post post = postMapper.toPost(postDto);

        when(postMapper.toPost(postDto)).thenReturn(post);
        when(projectServiceClient.getProject(postDto.getProjectId())).thenReturn(projectDto);

        postService.createPost(postDto);

        verify(postRepository, times(1)).save(post);

    }

    @Test
    void testFindingPostById_IllegalArgumentException() {
        long postId = 1;
        Post post = createPost(1L, "assd", 1L);
        post.setPublished(true);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        assertThrows(IllegalArgumentException.class, () -> postService.publishPost(postId));
    }

    @Test
    void testPublishedPost() {
        long postId = 1;
        Post post = createPost(1L, "assd", 1L);
        post.setPublished(false);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        postService.publishPost(postId);

        verify(postRepository, times(1))
                .updateIsPublished(anyLong(), anyBoolean(), any(LocalDateTime.class));
    }

    @Test
    void testUpdatingPost() {
        long postId = 1;
        PostDto postDto = new PostDto();
        postDto.setId(postId);
        postDto.setContent("assd_1");
        postDto.setAuthorId(1L);
        Post postBD = createPost(1L, "assa_2", 1L);
        Post postUser = postMapper.toPost(postDto);

        when(postMapper.toPost(postDto)).thenReturn(postUser);
        when(postRepository.findById(postId)).thenReturn(Optional.of(postBD));

        postService.updatePost(postDto);

        verify(postRepository, times(1)).deleteById(postBD.getId());
        verify(postRepository, times(1)).save(postUser);
    }

    @Test
    void testDeletingPost() {
        long postId = 1;
        Post post = createPost(1L, "assa_2", 1L);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        postService.deletePost(postId);

        verify(postRepository, times(1)).updateIsDeleted(postId, true);
    }

    @Test
    void testGettingPost() {
        long postId = 1;
        Post post = createPost(1L, "assa_2", 1L);
        PostDto postExpected = postMapper.toPostDto(post);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        PostDto postActual = postService.getPost(postId);

        assertEquals(postExpected, postActual);
    }

    @Test
    void testGettingPostDraftsByAuthorId() {
        long authorId = 1;
        List<Post> posts = List.of(
                createPostModern(1L, "post_1", authorId, null, false, false,
                        LocalDateTime.of(2016, Month.MAY, 7, 7, 15)),
                createPostModern(2L, "post_2", authorId, null, false, false,
                        LocalDateTime.of(2014, Month.MAY, 7, 7, 15)),
                createPostModern(3L, "post_3", authorId, null, false, false,
                        LocalDateTime.of(2015, Month.MAY, 7, 7, 15)),
                createPostModern(4L, "post_4", authorId, null, true, false,
                        LocalDateTime.of(2011, Month.MAY, 7, 7, 15))
                );
        List<PostDto> postExpected = streamListPostDto(posts, false, false);

        when(postRepository.findByAuthorId(authorId)).thenReturn(posts);

        List<PostDto> postActual = postService.getPostDraftsByAuthorId(authorId);

        assertEquals(postExpected, postActual);
    }

    @Test
    void testGettingPostDraftsByProjectId() {
        long projectId = 1;
        List<Post> posts = List.of(
                createPostModern(1L, "post_1", null, projectId, false, false,
                        LocalDateTime.of(2016, Month.MAY, 7, 7, 15)),
                createPostModern(2L, "post_2", null, projectId, false, false,
                        LocalDateTime.of(2014, Month.MAY, 7, 7, 15)),
                createPostModern(3L, "post_3", null, projectId, false, false,
                        LocalDateTime.of(2015, Month.MAY, 7, 7, 15)),
                createPostModern(4L, "post_4", null, projectId, true, false,
                        LocalDateTime.of(2011, Month.MAY, 7, 7, 15))
        );
        List<PostDto> postExpected = streamListPostDto(posts, false, false);

        when(postRepository.findByProjectId(projectId)).thenReturn(posts);

        List<PostDto> postActual = postService.getPostDraftsByProjectId(projectId);

        assertEquals(postExpected, postActual);
    }

    @Test
    void testGettingPostPublishedByAuthorId() {
        long authorId = 1;
        List<Post> posts = List.of(
                createPostModern(1L, "post_1", authorId, null, true, false,
                        LocalDateTime.of(2016, Month.MAY, 7, 7, 15)),
                createPostModern(2L, "post_2", authorId, null, true, false,
                        LocalDateTime.of(2014, Month.MAY, 7, 7, 15)),
                createPostModern(3L, "post_3", authorId, null, false, false,
                        LocalDateTime.of(2015, Month.MAY, 7, 7, 15)),
                createPostModern(4L, "post_4", authorId, null, true, false,
                        LocalDateTime.of(2011, Month.MAY, 7, 7, 15))
        );
        List<PostDto> postExpected = streamListPostDto(posts, true, false);

        when(postRepository.findByAuthorId(authorId)).thenReturn(posts);

        List<PostDto> postActual = postService.getPostPublishedByAuthorId(authorId);

        assertEquals(postExpected, postActual);
    }

    @Test
    void testGettingPostPublishedByProjectId() {
        long projectId = 1;
        List<Post> posts = List.of(
                createPostModern(1L, "post_1", null, projectId, true, false,
                        LocalDateTime.of(2016, Month.MAY, 7, 7, 15)),
                createPostModern(2L, "post_2", null, projectId, true, false,
                        LocalDateTime.of(2014, Month.MAY, 7, 7, 15)),
                createPostModern(3L, "post_3", null, projectId, false, false,
                        LocalDateTime.of(2015, Month.MAY, 7, 7, 15)),
                createPostModern(4L, "post_4", null, projectId, true, false,
                        LocalDateTime.of(2011, Month.MAY, 7, 7, 15))
        );
        List<PostDto> postExpected = streamListPostDto(posts, true, false);

        when(postRepository.findByProjectId(projectId)).thenReturn(posts);

        List<PostDto> postActual = postService.getPostPublishedByProjectId(projectId);

        assertEquals(postExpected, postActual);
    }

    @NotNull
    private Post createPost(Long id, String context, Long authorId) {
        Post post = new Post();
        post.setId(id);
        post.setContent(context);
        post.setAuthorId(authorId);

        return post;
    }

    @NotNull
    private Post createPostModern(Long id, String context, Long authorId, Long projectId,
                                  boolean published, boolean deleted, LocalDateTime time) {
        Post post = new Post();
        post.setId(id);
        post.setContent(context);
        post.setAuthorId(authorId);
        post.setProjectId(projectId);
        post.setPublished(published);
        post.setDeleted(deleted);
        post.setCreatedAt(time);

        return post;
    }

    @NotNull
    private List<PostDto> streamListPostDto(List<Post> posts, boolean published, boolean deleted) {
        return posts.stream()
                .filter(post -> post.isPublished() == published && post.isDeleted() == deleted)
                .sorted(Comparator.comparing(Post::getCreatedAt))
                .map(postMapper::toPostDto)
                .toList();
    }
}
