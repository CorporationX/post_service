package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.utils.Spelling;
import faang.school.postservice.validator.PostValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    @Mock
    private AsyncPostPublishService asyncPostPublishService;
    @Mock
    private Spelling spelling;

    @InjectMocks
    private PostService postService;
    private final PostDto postDto = new PostDto();


    @Test
    void createDraftWithAuthorTest() {
        postDto.setAuthorId(1L);
        when(userServiceClient.getUser(postDto.getAuthorId())).thenReturn(null);

        postService.createDraftPost(postDto, null);
        Mockito.verify(userServiceClient, Mockito.times(1)).getUser(postDto.getAuthorId());
    }

    @Test
    void createDraftWithProjectTest() {
        postDto.setProjectId(1L);
        when(projectServiceClient.getProject(postDto.getProjectId())).thenReturn(null);

        postService.createDraftPost(postDto, null);
        Mockito.verify(projectServiceClient, Mockito.times(1)).getProject(postDto.getProjectId());
    }

    @Test
    void createDraftWithAuthorAndProjectTest() {
        postDto.setAuthorId(1L);
        postDto.setProjectId(1L);

        when(userServiceClient.getUser(postDto.getAuthorId())).thenReturn(null);
        Mockito.doThrow(new DataValidationException("У поста должен быть только один автор"))
                .when(postValidator).validateAuthorExists(any(), any());

        assertThrows(DataValidationException.class, () -> postService.createDraftPost(postDto, null));
    }

    @Test
    void createDraftWithCorrectDataTest() {
        postDto.setAuthorId(1L);

        when(userServiceClient.getUser(postDto.getAuthorId()))
                .thenReturn(new UserDto(1L, "user1", "user1@mail"));
        postService.createDraftPost(postDto, null);

        Mockito.verify(postRepository, Mockito.times(1)).save(any());
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
                .when(postValidator).validateIsNotPublished(any());

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
        UpdatePostDto updatedPostDto = new UpdatePostDto();
        Post post = new Post();
        post.setId(1L);
        post.setAuthorId(1L);
        updatedPostDto.setId(1L);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        postService.updatePost(updatedPostDto, 1L, null);

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
        newDraftPost.setLikes(new ArrayList<>());
        Post oldDraftPost = new Post();
        oldDraftPost.setCreatedAt(LocalDateTime.of(2021, 1, 1, 0, 0));
        oldDraftPost.setLikes(new ArrayList<>());
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
        newDraftPost.setLikes(new ArrayList<>());
        Post oldDraftPost = new Post();
        oldDraftPost.setCreatedAt(LocalDateTime.of(2021, 1, 1, 0, 0));
        oldDraftPost.setLikes(new ArrayList<>());
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

    @Test
    public void publishScheduledPosts() {
        //Arrange
        ReflectionTestUtils.setField(postService, "sizeSublist", 100);
        List<Post> posts = List.of(
                Post.builder().content("text").authorId(1L).published(false).build()
        );
        when(postRepository.findReadyToPublish()).thenReturn(posts);

        //Act
        postService.publishScheduledPosts();

        //Assert
        verify(postRepository, times(1)).findReadyToPublish();
        verify(asyncPostPublishService, times(1)).publishPost(any());
    }

    @Test
    public void correctPost_when() {
        //Arrange
        String content = "Прывет";
        String correctContent = "Привет";
        Post post = Post.builder().content(content).authorId(1L).published(false).checkSpelling(false).build();

        List<Post> posts = List.of(post);
        when(postRepository.findReadyToPublish()).thenReturn(posts);
        when(spelling.check(content))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(correctContent)));

        //Act
        postService.correctPost();

        //Assert
        verify(postRepository, times(1)).findReadyToPublish();
        assertEquals(post.getContent(), correctContent);
    }
}