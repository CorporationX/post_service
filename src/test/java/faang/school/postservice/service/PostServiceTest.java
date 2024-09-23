package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.service.post.checker.AuthorUpdatedPostChecker;
import faang.school.postservice.service.post.checker.ProjectUpdatedPostChecker;
import faang.school.postservice.service.post.checker.UpdatedPostChecker;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    private PostService service;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectServiceClient projectServiceClient;
    @Mock
    private PostRepository postRepository;
    @Spy
    private PostMapper mapper = Mappers.getMapper(PostMapper.class);
    private List<UpdatedPostChecker> updatedPostCheckers = new ArrayList<>(
            List.of(
            new AuthorUpdatedPostChecker(),
            new ProjectUpdatedPostChecker()
    ));

    private PostDto postDto;
    private PostDto expectedPostDto;
    private Post expectedCapturedPost;
    private Post returnedPost;
    private UserDto userDto;
    private ProjectDto projectDto;
    private List<Post> returnedPosts;
    private List<PostDto> expectedPostDtos;
    @Captor
    private ArgumentCaptor<Post> postCaptor;

    @BeforeEach
    public void setup() {
        service = new PostService(
            postRepository,
            mapper,
            updatedPostCheckers,
            userServiceClient,
            projectServiceClient
        );
        userDto =  new UserDto(1L, "username", "email");
        projectDto = new ProjectDto();

        postDto = null;
        expectedPostDto = null;
        expectedCapturedPost = null;
        returnedPost = null;
        returnedPosts = new ArrayList<>(
                List.of(
                        Post.builder()
                                .id(1L)
                                .content("content1")
                                .createdAt(LocalDateTime.of(2024,4, 1, 1, 1, 1))
                                .publishedAt(LocalDateTime.of(2024,4, 1, 1, 1, 1))
                                .build(),
                        Post.builder()
                                .id(2L)
                                .content("content2")
                                .createdAt(LocalDateTime.of(2024,7, 1, 1, 1, 1))
                                .publishedAt(LocalDateTime.of(2024,7, 1, 1, 1, 1))
                                .build(),
                        Post.builder()
                                .id(3L)
                                .content("content3")
                                .createdAt(LocalDateTime.of(2024,6, 1, 1, 1, 1))
                                .publishedAt(LocalDateTime.of(2024,6, 1, 1, 1, 1))
                                .build()
                )
        );
        expectedPostDtos = new ArrayList<>(
                List.of(
                        new PostDto(
                            2L,
                            "content2",
                            0,
                            0,
                            null,
                            null,
                             LocalDateTime.of(2024,7, 1, 1, 1, 1)
                        ),
                        new PostDto(
                                3L,
                                "content3",
                                0,
                                0,
                                null,
                                null,
                                LocalDateTime.of(2024,6, 1, 1, 1, 1)
                        ),
                        new PostDto(
                                1L,
                                "content1",
                                0,
                                0,
                                null,
                                null,
                                LocalDateTime.of(2024,4, 1, 1, 1, 1)
                        )

                )
        );
    }

    @Test
    public void testCreate_AuthorNotExistInDb() {
        // Arrange
        postDto = createPostDto(1L, 0, null);
        when(userServiceClient.getUser(postDto.authorId())).thenReturn(null);

        // Act and Assert
        Exception exception = Assertions.assertThrows(DataValidationException.class, () -> service.create(postDto));
        Assertions.assertEquals("Пользователь с id = " + postDto.authorId() + " не найден в системе", exception.getMessage());
    }

    @Test
    public void testCreate_ProjectNotExistInDb() {
        // Arrange
        postDto = createPostDto(0, 1L, null);
        when(projectServiceClient.getProject(postDto.projectId())).thenReturn(null);

        // Act and Assert
        Exception exception = Assertions.assertThrows(DataValidationException.class, () -> service.create(postDto));
        Assertions.assertEquals("Проект с id = " + postDto.projectId() + " не найден в системе", exception.getMessage());
    }

    @Test
    public void testCreate_SuccessWithAuthor() {
        // Arrange
        var authorId = 1L;
        var projectId = 0L;
        postDto = createPostDto(authorId, projectId, LocalDateTime.now());
        expectedCapturedPost = createPost(authorId, projectId, null);
        returnedPost = createPost(authorId, projectId, LocalDateTime.now());
        expectedPostDto = createPostDto(authorId, projectId, returnedPost.getPublishedAt());
        when(userServiceClient.getUser(postDto.authorId())).thenReturn(userDto);;
        when(postRepository.save(any())).thenReturn(returnedPost);;

        // Act and Assert
        PostDto returnPostDto = service.create(postDto);
        verify(postRepository, times(1)).save(postCaptor.capture());
        Assertions.assertEquals(expectedCapturedPost, postCaptor.getValue());
        Assertions.assertEquals(expectedPostDto, returnPostDto);
    }

    @Test
    public void testCreate_SuccessWithProject() {
        // Arrange
        var authorId = 0L;
        var projectId = 1L;
        postDto = createPostDto(authorId, projectId, LocalDateTime.now());
        expectedCapturedPost = createPost(authorId, projectId, null);
        returnedPost = createPost(authorId, projectId, LocalDateTime.now());
        expectedPostDto = createPostDto(authorId, projectId, returnedPost.getPublishedAt());
        when(projectServiceClient.getProject(postDto.projectId())).thenReturn(projectDto);;
        when(postRepository.save(any())).thenReturn(returnedPost);;

        // Act and Assert
        PostDto returnPostDto = service.create(postDto);
        verify(postRepository, times(1)).save(postCaptor.capture());
        Assertions.assertEquals(expectedCapturedPost, postCaptor.getValue());
        Assertions.assertEquals(expectedPostDto, returnPostDto);
    }

    @Test
    public void testPublish_PostNotExistInDb() {
        // Arrange
        var id = 1L;
        when(postRepository.findById(any())).thenReturn(Optional.empty());

        // Act and Assert
        Exception exception = Assertions.assertThrows(EntityNotFoundException.class, () -> service.publish(id));
        Assertions.assertEquals("Пост с id = " + id + " не существует в системе", exception.getMessage());
    }

    @Test
    public void testPublish_PostDeletedInDb() {
        // Arrange
        var id = 1L;
        when(postRepository.findById(id)).thenReturn(Optional.of(Post.builder().id(id).deleted(true).build()));

        // Act and Assert
        Exception exception = Assertions.assertThrows(EntityNotFoundException.class, () -> service.publish(id));
        Assertions.assertEquals("Пост с id = " + id + " не существует в системе", exception.getMessage());
    }

    @Test
    public void testPublish_PostAlreadyPublished() {
        // Arrange
        var id = 1L;
        var authorId = 1L;
        var projectId = 0L;
        returnedPost = createPost(authorId, projectId, LocalDateTime.now());
        returnedPost.setPublished(true);
        when(postRepository.findById(id)).thenReturn(Optional.of(returnedPost));

        // Act and Assert
        Exception exception = Assertions.assertThrows(DataValidationException.class, () -> service.publish(id));
        Assertions.assertEquals("Пост с id = " + id + " уже был опубликован", exception.getMessage());
    }

    @Test
    public void testPublish() {
        // Arrange
        var id = 1L;
        var authorId = 1L;
        var projectId = 0L;
        returnedPost = createPost(authorId, projectId, LocalDateTime.now());
        expectedCapturedPost = createPost(authorId, projectId, null);
        expectedCapturedPost.setPublished(true);
        when(postRepository.findById(id)).thenReturn(Optional.of(returnedPost));
        when(postRepository.save(any())).thenReturn(returnedPost);;

        // Act and Assert
        PostDto returnPostDto = service.publish(id);
        verify(postRepository, times(1)).save(postCaptor.capture());
        Post capturedPost = postCaptor.getValue();
        Assertions.assertNotNull(capturedPost.getPublishedAt());
        expectedPostDto = createPostDto(authorId, projectId, capturedPost.getPublishedAt());
        capturedPost.setPublishedAt(null);
        Assertions.assertEquals(expectedCapturedPost, capturedPost);
        Assertions.assertEquals(expectedPostDto, returnPostDto);
    }

    @Test
    public void testUpdate_ChangeAuthor() {
        // Arrange
        postDto = createPostDto(1L, 0, LocalDateTime.now());
        returnedPost = createPost(2L, 0, postDto.publishedAt());
        when(postRepository.findById(postDto.id())).thenReturn(Optional.of(returnedPost));

        // Act and Assert
        Exception exception = Assertions.assertThrows(DataValidationException.class, () -> service.update(postDto));
        Assertions.assertEquals("Нельзя изменить автора поста", exception.getMessage());
    }

    @Test
    public void testUpdate_ChangeProject() {
        // Arrange
        postDto = createPostDto(0, 1L, LocalDateTime.now());
        returnedPost = createPost(0, 2L, postDto.publishedAt());
        when(postRepository.findById(postDto.id())).thenReturn(Optional.of(returnedPost));

        // Act and Assert
        Exception exception = Assertions.assertThrows(DataValidationException.class, () -> service.update(postDto));
        Assertions.assertEquals("Нельзя изменить автора(проект) поста", exception.getMessage());
    }

    @Test
    public void testUpdate() {
        // Arrange
        var authorId = 0;
        var projectId = 1;
        postDto = createPostDto(authorId, projectId, LocalDateTime.now());
        expectedPostDto = createPostDto(authorId, projectId, LocalDateTime.now());
        returnedPost = createPost(authorId, projectId, postDto.publishedAt());
        returnedPost.setContent("oldContent");
        when(postRepository.findById(postDto.id())).thenReturn(Optional.of(returnedPost));
        Post returnedPostForSave = createPost(0, 1L, postDto.publishedAt());
        when(postRepository.save(any())).thenReturn(returnedPostForSave);
        expectedCapturedPost = createPost(authorId, projectId, postDto.publishedAt());

        // Act and Assert
        PostDto returnPostDto = service.update(postDto);
        verify(postRepository, times(1)).save(postCaptor.capture());
        Assertions.assertEquals(expectedCapturedPost, postCaptor.getValue());
        Assertions.assertEquals(expectedPostDto, returnPostDto);
    }

    @Test
    public void testSoftlyDelete() {
        // Arrange
        var id = 1L;
        returnedPost = createPost(1L, 0, null);
        expectedCapturedPost = createPost(1L, 0, null);
        expectedCapturedPost.setDeleted(true);
        when(postRepository.findById(id)).thenReturn(Optional.of(returnedPost));

        // Act and Assert
        service.softlyDelete(id);
        verify(postRepository, times(1)).save(postCaptor.capture());
        Assertions.assertEquals(expectedCapturedPost, postCaptor.getValue());
    }

    @Test
    public void testGetPost() {
        // Arrange
        var id = 1L;
        returnedPost = createPost(1L, 0, LocalDateTime.now());
        expectedPostDto = createPostDto(1L, 0, returnedPost.getPublishedAt());
        when(postRepository.findById(id)).thenReturn(Optional.of(returnedPost));

        // Act
        PostDto returnPostDto = service.getPost(id);

        // Assert
        Assertions.assertEquals(expectedPostDto, returnPostDto);

    }

    @Test
    public void testGetDraftPostsForUser() {
        // Arrange
        var authorId = 1L;
        when(userServiceClient.getUser(authorId)).thenReturn(userDto);
        when(postRepository.findPostsByAuthorId(authorId, false)).thenReturn(returnedPosts);

        // Act
        List<PostDto> returnPosts = service.getDraftPostsForUser(authorId);

        // Assert
        Assertions.assertEquals(expectedPostDtos, returnPosts);

    }

    @Test
    public void testGetDraftPostsForProject() {
        // Arrange
        var projectId = 1L;
        when(projectServiceClient.getProject(projectId)).thenReturn(projectDto);
        when(postRepository.findPostsByProjectId(projectId, false)).thenReturn(returnedPosts);

        // Act
        List<PostDto> returnPosts = service.getDraftPostsForProject(projectId);

        // Assert
        Assertions.assertEquals(expectedPostDtos, returnPosts);

    }

    @Test
    public void testGetPublishedPostsForUser() {
        // Arrange
        var authorId = 1L;
        when(userServiceClient.getUser(authorId)).thenReturn(userDto);
        when(postRepository.findPostsByAuthorId(authorId, true)).thenReturn(returnedPosts);

        // Act
        List<PostDto> returnPosts = service.getPublishedPostsForUser(authorId);

        // Assert
        Assertions.assertEquals(expectedPostDtos, returnPosts);

    }

    @Test
    public void testGetPublishedPostsForProject() {
        // Arrange
        var projectId = 1L;
        when(projectServiceClient.getProject(projectId)).thenReturn(projectDto);
        when(postRepository.findPostsByProjectId(projectId, true)).thenReturn(returnedPosts);

        // Act
        List<PostDto> returnPosts = service.getPublishedPostsForProject(projectId);

        // Assert
        Assertions.assertEquals(expectedPostDtos, returnPosts);

    }

    private PostDto createPostDto(long authorId, long projectId, LocalDateTime publishedAt) {
        return new PostDto(
                1,
                "content",
                authorId,
                projectId,
                new ArrayList<>(List.of(1L, 2L, 3L)),
                new ArrayList<>(List.of(1L, 2L, 3L)),
                publishedAt
        );
    }

    private Post createPost(long authorId, long projectId, LocalDateTime publishedAt) {
        return Post.builder()
                .id(1)
                .content("content")
                .authorId(authorId)
                .projectId(projectId)
                .publishedAt(publishedAt)
                .comments(
                        new ArrayList<>(List.of(
                                Comment.builder().id(1L).build(),
                                Comment.builder().id(2L).build(),
                                Comment.builder().id(3L).build()
                        ))
                )
                .likes(
                        new ArrayList<>(List.of(
                                Like.builder().id(1L).build(),
                                Like.builder().id(2L).build(),
                                Like.builder().id(3L).build()
                        ))
                )
                .build();
    }
}
