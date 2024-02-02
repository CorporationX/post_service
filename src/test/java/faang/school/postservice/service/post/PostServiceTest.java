package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.post.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private PostValidator postValidator;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectServiceClient projectServiceClient;
    @Spy
    private PostMapperImpl postMapper = new PostMapperImpl();

    @InjectMocks
    private PostService postService;

    private final PostDto postDto = new PostDto();



    @Test
    void createDraftWithAuthorTest() {
        postDto.setAuthorId(1L);
        when(userServiceClient.getUser(postDto.getAuthorId())).thenReturn(null);

        postService.createDraftPost(postDto);
        Mockito.verify(userServiceClient, Mockito.times(1)).getUser(postDto.getAuthorId());
    }

    @Test
    void createDraftWithProjectTest() {
        postDto.setProjectId(1L);
        when(projectServiceClient.getProject(postDto.getProjectId())).thenReturn(null);

        postService.createDraftPost(postDto);
        Mockito.verify(projectServiceClient, Mockito.times(1)).getProject(postDto.getProjectId());
    }

    @Test
    void createDraftWithAuthorAndProjectTest() {
        postDto.setAuthorId(1L);
        postDto.setProjectId(1L);

        when(userServiceClient.getUser(postDto.getAuthorId())).thenReturn(null);
        Mockito.doThrow(new DataValidationException("У поста должен быть только один автор"))
                .when(postValidator).validateAuthorExists(Mockito.any(), Mockito.any());

        assertThrows(DataValidationException.class, () -> postService.createDraftPost(postDto));
    }

    @Test
    void createDraftWithNonExistingCreatorTest() {
        assertThrows(IllegalArgumentException.class, ()-> postService.createDraftPost(postDto));
    }

    @Test
    void createDraftWithCorrectDataTest() {
        postDto.setAuthorId(1L);

        when(userServiceClient.getUser(postDto.getAuthorId()))
                .thenReturn(new UserDto(1L, "user1", "user1@mail"));
        postService.createDraftPost(postDto);

        Mockito.verify(postRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void publishPostIncorrectIdTest() {
        long id = 1L;
        when(postRepository.findById(1L))
                .thenThrow(new EntityNotFoundException("Пост с указанным ID не существует"));
        assertThrows(EntityNotFoundException.class, () -> postService.publishPost(id));
    }

    @Test
    void publishAPublishedPostTest() {
        long id = 1L;
        Post post = new Post();
        post.setId(id);

        when(postRepository.findById(id)).thenReturn(Optional.of(post));
        Mockito.doThrow(new DataValidationException("Пост уже опубликован"))
                .when(postValidator).validateIsNotPublished(Mockito.any());

        assertThrows(DataValidationException.class, () -> postService.publishPost(id));
    }

    @Test
    void publishCorrectPostTest() {
        long id = 1L;
        Post post = new Post();
        post.setId(id);

        when(postRepository.findById(id)).thenReturn(Optional.of(post));
        postService.publishPost(id);
        Mockito.verify(postRepository, Mockito.times(1)).save(post);
    }

    @Test
    void updateCorrectPostTest() {
        Post post = new Post();
        post.setId(1L);
        post.setAuthorId(1L);
        postDto.setAuthorId(1L);
        postDto.setId(1L);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        postService.updatePost(postDto);

        verify(postRepository, Mockito.times(1)).save(post);
    }

    @Test
    void deleteIncorrectPostTest() {
        when(postRepository.findById(1L))
                .thenThrow(new EntityNotFoundException("Пост с указанным ID не существует"));
        assertThrows(EntityNotFoundException.class, () -> postService.deletePost(1L));
    }

    @Test
    void deleteAlreadyDeletedPostTest() {
        Post post = new Post();
        post.setId(1L);
        post.setDeleted(true);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        assertThrows(DataValidationException.class, () -> postService.deletePost(1L));
    }

    @Test
    void deleteCorrectPostTest() {
        Post post = new Post();
        post.setId(1L);
        post.setDeleted(false);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        postService.deletePost(1L);

        assertTrue(post.isDeleted());
    }

    @Test
    void shouldGetDraftPostsByUser() {
        Post deletedPost = new Post();
        deletedPost.setDeleted(true);
        Post publishedPost = new Post();
        publishedPost.setPublished(true);
        Post newDraftPost = new Post();
        newDraftPost.setCreatedAt(LocalDateTime.of(2024, 1, 1, 0, 0));
        Post oldDraftPost = new Post();
        oldDraftPost.setCreatedAt(LocalDateTime.of(2021, 1, 1, 0, 0));
        List<Post> AllUserPosts = List.of(deletedPost, publishedPost, newDraftPost, oldDraftPost);

        when(postRepository.findByAuthorId(1L)).thenReturn(AllUserPosts);
        List<PostDto> result = postService.getDraftsByUser(1L);

        assertEquals(2, result.size());
        assertEquals(postMapper.toDto(newDraftPost), result.get(0));
        assertEquals(postMapper.toDto(oldDraftPost), result.get(1));
    }

    @Test
    void shouldGetDraftPostsByProject() {
        Post deletedPost = new Post();
        deletedPost.setDeleted(true);
        Post publishedPost = new Post();
        publishedPost.setPublished(true);
        Post newDraftPost = new Post();
        newDraftPost.setCreatedAt(LocalDateTime.of(2024, 1, 1, 0, 0));
        Post oldDraftPost = new Post();
        oldDraftPost.setCreatedAt(LocalDateTime.of(2021, 1, 1, 0, 0));
        List<Post> AllProjectPosts = List.of(deletedPost, publishedPost, newDraftPost, oldDraftPost);

        when(postRepository.findByProjectId(1L)).thenReturn(AllProjectPosts);
        List<PostDto> result = postService.getDraftsByProject(1L);

        assertEquals(2, result.size());
        assertEquals(postMapper.toDto(newDraftPost), result.get(0));
        assertEquals(postMapper.toDto(oldDraftPost), result.get(1));
    }

    @Test
    void shouldGetPublishedPostsByUser() {
        Post deletedPost = new Post();
        deletedPost.setDeleted(true);
        Post draftPost = new Post();
        draftPost.setPublished(false);
        Post newPublishedPost = new Post();
        newPublishedPost.setPublished(true);
        newPublishedPost.setPublishedAt(LocalDateTime.of(2024, 1, 1, 0, 0));
        newPublishedPost.setLikes(new ArrayList<>());
        Post oldPublishedPost = new Post();
        oldPublishedPost.setPublished(true);
        oldPublishedPost.setPublishedAt(LocalDateTime.of(2021, 1, 1, 0, 0));
        oldPublishedPost.setLikes(new ArrayList<>());
        List<Post> AllProjectPosts = List.of(deletedPost, newPublishedPost, draftPost, oldPublishedPost);

        when(postRepository.findByAuthorIdWithLikes(1L)).thenReturn(AllProjectPosts);
        List<PostDto> result = postService.getPublishedPostsByUser(1L);

        assertEquals(2, result.size());
        assertEquals(postMapper.toDto(newPublishedPost), result.get(0));
        assertEquals(postMapper.toDto(oldPublishedPost), result.get(1));
    }

    @Test
    void shouldGetPublishedPostsByProject() {
        Post deletedPost = new Post();
        deletedPost.setDeleted(true);

        Post draftPost = new Post();
        draftPost.setPublished(false);

        Post newPublishedPost = new Post();
        newPublishedPost.setPublished(true);
        newPublishedPost.setPublishedAt(LocalDateTime.of(2024, 1, 1, 0, 0));
        newPublishedPost.setLikes(new ArrayList<>());

        Post oldPublishedPost = new Post();
        oldPublishedPost.setPublished(true);
        oldPublishedPost.setPublishedAt(LocalDateTime.of(2021, 1, 1, 0, 0));
        oldPublishedPost.setLikes(new ArrayList<>());

        List<Post> AllProjectPosts = List.of(deletedPost, newPublishedPost, draftPost, oldPublishedPost);

        when(postRepository.findByProjectIdWithLikes(1L)).thenReturn(AllProjectPosts);
        List<PostDto> result = postService.getPublishedPostsByProject(1L);

        assertEquals(2, result.size());
        assertEquals(postMapper.toDto(newPublishedPost), result.get(0));
        assertEquals(postMapper.toDto(oldPublishedPost), result.get(1));
    }
}