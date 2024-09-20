package faang.school.postservice.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataDoesNotExistException;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Spy
    private PostMapperImpl postMapper;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectServiceClient projectServiceClient;
    @InjectMocks
    private PostService service;

    @Test
    public void testCreateDraftPost() {
        PostDto postDto = PostDto.builder()
                .id(1L)
                .authorId(1L)
                .content("Test")
                .build();
        Post post = new Post();
        post.setId(1L);
        post.setContent("Test");
        post.setAuthorId(1L);
        UserDto userDto = new UserDto(1L, "Name", "Email");

        when(postMapper.toEntity(any())).thenReturn(post);
        when(userServiceClient.getUser(post.getId())).thenReturn(userDto);
        when(postMapper.toDto(post)).thenReturn(postDto);
        PostDto resultDto = service.createDraftPost(postDto);
        verify(postRepository, times(1)).save(post);
        assertEquals(postDto.getId(), resultDto.getId());
        assertEquals(postDto.getContent(), resultDto.getContent());
        assertEquals(postDto.getAuthorId(), resultDto.getAuthorId());
    }

    @Test
    public void testPublishDraftPost() {
        Optional<Post> post = Optional.of(new Post());
        when(postRepository.findById(1L)).thenReturn(post);
        PostDto resultPostDto = service.publishPost(1L);
        verify(postRepository, times(1)).save(post.get());
        assertTrue(resultPostDto.isPublished());
    }

    @Test
    public void testPostDoesNotExist() {
        checkPostForExistenceInDB(() -> service.publishPost(1L));
    }

    @Test
    public void testUserDoesNotExistCreatingPost() {
        PostDto dto = PostDto.builder()
                .authorId(1L)
                .build();
        Post post = new Post();
        post.setId(1L);
        post.setAuthorId(1L);
        UserDto userDto = new UserDto(null, null, null);
        when(postMapper.toEntity(dto)).thenReturn(post);
        when(userServiceClient.getUser(post.getId())).thenReturn(userDto);
        assertThrows(DataDoesNotExistException.class, () -> service.createDraftPost(dto));
    }

    @Test
    public void testProjectDoesNotExistCreatingPost() {
        PostDto dto = PostDto.builder()
                .projectId(1L)
                .build();
        Post post = new Post();
        post.setProjectId(1L);
        ProjectDto projectDto = new ProjectDto();
        when(postMapper.toEntity(dto)).thenReturn(post);
        when(projectServiceClient.getProject(post.getProjectId())).thenReturn(projectDto);
        assertThrows(DataDoesNotExistException.class, () -> service.createDraftPost(dto));
    }

    @Test
    public void testUpdatePost() {
        Optional<Post> post = Optional.of(new Post());
        post.get().setId(1L);
        when(postRepository.findById(1L)).thenReturn(post);
        PostDto postDto = PostDto.builder()
                .id(1L)
                .content("Test")
                .build();
        PostDto resultPostDto = service.updatePost(1L, postDto);
        verify(postRepository, times(1)).save(post.get());
        assertEquals(postDto.getContent(), resultPostDto.getContent());
    }

    @Test
    public void testPostDoesNotExistUpdatingPost() {
        PostDto dto = PostDto.builder()
                .build();
        checkPostForExistenceInDB(() -> service.updatePost(1L, dto));
    }

    @Test
    public void testDeletePost() {
        Optional<Post> post = Optional.of(new Post());
        post.get().setId(1L);
        when(postRepository.findById(1L)).thenReturn(post);
        PostDto resultPostDto = service.deletePost(1L);
        verify(postRepository, times(1)).save(post.get());
        assertTrue(resultPostDto.isDeleted());
    }

    @Test
    public void testPostDoesNotExistDeletingPost() {
        checkPostForExistenceInDB(() -> service.deletePost(1L));
    }

    @Test
    public void testGetPost() {
        List<Like> likes = List.of(
                Like.builder()
                        .id(11L)
                        .build(),
                Like.builder()
                        .id(12L)
                        .build()
        );

        Optional<Post> post = Optional.of(new Post());
        post.get().setId(1L);
        post.get().setLikes(likes);

        when(postRepository.findById(1L)).thenReturn(post);

        PostDto resultPostDto = service.getPost(1L);

        assertEquals(post.get().getId(), resultPostDto.getId());
        assertEquals(2, resultPostDto.getLikes());
    }

    @Test
    public void testGetPostNotFound() {
        checkPostForExistenceInDB(() -> service.getPost(1L));
    }

    @Test
    public void testGetAllDraftsByUser() {
        PostDto postDto = PostDto.builder()
                .authorId(1L)
                .published(false)
                .build();
        List<Post> posts = initPostsData();
        when(postRepository.findByAuthorId(1L)).thenReturn(posts);
        List<PostDto> filteredList = service.getPostsSortedByDate(postDto);
        assertEquals(filteredList.size(), 2);
        assertTrue(filteredList.get(0).getCreatedAt().isBefore(filteredList.get(1).getCreatedAt()));
    }

    @Test
    public void testGetAllDraftsByProject() {
        PostDto postDto = PostDto.builder()
                .projectId(1L)
                .published(false)
                .build();
        List<Post> posts = initPostsData();
        when(postRepository.findByProjectId(1L)).thenReturn(posts);
        List<PostDto> filteredList = service.getPostsSortedByDate(postDto);
        assertEquals(filteredList.size(), 2);
        assertTrue(filteredList.get(0).getCreatedAt().isBefore(filteredList.get(1).getCreatedAt()));
    }

    @Test
    public void testGetAllPostsByUser() {
        PostDto postDto = PostDto.builder()
                .authorId(1L)
                .published(true)
                .build();
        List<Post> posts = initPostsData();
        when(postRepository.findByAuthorId(1L)).thenReturn(posts);
        List<PostDto> filteredList = service.getPostsSortedByDate(postDto);
        assertEquals(filteredList.size(), 1);
    }

    @Test
    public void testGetAllPostsByProject() {
        PostDto postDto = PostDto.builder()
                .projectId(1L)
                .published(true)
                .build();
        List<Post> posts = initPostsData();
        when(postRepository.findByProjectId(1L)).thenReturn(posts);
        List<PostDto> filteredList = service.getPostsSortedByDate(postDto);
        assertEquals(filteredList.size(), 1);
    }

    @Test
    public void testNoPostFromUserOrProject() {
        PostDto postDto = PostDto.builder()
                .authorId(1L)
                .build();
        when(postRepository.findByAuthorId(1L)).thenReturn(List.of());
        assertThrows(DataDoesNotExistException.class, () -> service.getPostsSortedByDate(postDto));
    }

    private List<Post> initPostsData() {
        Post firstPost = new Post();
        Post secondPost = new Post();
        Post thirdPost = new Post();
        Post fourthPost = new Post();
        Post fifthPost = new Post();
        firstPost.setPublished(true);
        secondPost.setPublished(false);
        thirdPost.setPublished(true);
        fourthPost.setPublished(false);
        fifthPost.setPublished(true);
        firstPost.setDeleted(false);
        secondPost.setDeleted(false);
        thirdPost.setDeleted(true);
        fourthPost.setDeleted(false);
        fifthPost.setDeleted(true);
        firstPost.setCreatedAt(LocalDateTime.of(2024, Month.JANUARY, 15, 15, 20));
        secondPost.setCreatedAt(LocalDateTime.of(2024, Month.MARCH, 15, 15, 20));
        thirdPost.setCreatedAt(LocalDateTime.of(2024, Month.MARCH, 18, 15, 20));
        fourthPost.setCreatedAt(LocalDateTime.of(2024, Month.FEBRUARY, 9, 15, 20));
        fifthPost.setCreatedAt(LocalDateTime.of(2024, Month.FEBRUARY, 15, 16, 20));

        return List.of(firstPost, secondPost, thirdPost, fourthPost, fifthPost);
    }

    private void checkPostForExistenceInDB(Executable runnable) {
        Optional<Post> post = Optional.empty();
        when(postRepository.findById(1L)).thenReturn(post);
        assertThrows(DataDoesNotExistException.class, runnable);
    }
}
