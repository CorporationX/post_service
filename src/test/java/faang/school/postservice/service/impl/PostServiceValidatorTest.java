package faang.school.postservice.service.impl;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostCreateRequestDto;
import faang.school.postservice.dto.project.ProjectResponseDto;
import faang.school.postservice.dto.user.UserResponseDto;
import faang.school.postservice.model.Post;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostServiceValidatorTest {

    @InjectMocks
    private PostServiceValidator postServiceValidator;
    @Mock
    private ProjectServiceClient projectServiceClient;
    @Mock
    private UserServiceClient userServiceClient;


    PostCreateRequestDto validPostCreateRequestDto;
    //UserDto userDto;
    UserResponseDto userDto;
    ProjectResponseDto projectDto;
    Post post;
    Post postByOtherAuthor;
    Post postInOtherProject;

    @BeforeEach
    void setUp() {
        validPostCreateRequestDto = PostCreateRequestDto.builder()
                .content("test content")
                .authorId(111L)
                .projectId(222L)
                .build();
        post = Post.builder()
                .id(123L)
                .content("some content")
                .authorId(111L)
                .projectId(123L)
                .build();
        postByOtherAuthor = Post.builder()
                .id(123L)
                .content("some content")
                .authorId(222L)
                .projectId(123L)
                .build();
        postInOtherProject = Post.builder()
                .id(123L)
                .content("some content")
                .authorId(111L)
                .projectId(1231L)
                .build();
        //userDto = new UserResponseDto(111L, "Alice", "alice@mail.ru");
        userDto = new UserResponseDto(
                111L,
                "Alice",
                "alice@mail.ru",
                "12221",
                UserResponseDto.PreferredContact.EMAIL);
        projectDto = new ProjectResponseDto(222L, 1L,"New project", "new", "test");
    }

    @Test
    @DisplayName("Test authorship for post")
    void testValidateAuthorshipPostDto() {
        PostCreateRequestDto emptyAuthorsPostDto = PostCreateRequestDto.builder()
                .content("test")
                .build();
        postServiceValidator.validatePostDto(emptyAuthorsPostDto);

        long authorId = 0L;
        PostCreateRequestDto emptyAuthorIdIsZeroPostDto = PostCreateRequestDto.builder()
                .authorId(authorId)
                .content("test")
                .build();
        Mockito.when(userServiceClient.getUser(authorId)).thenReturn(userDto);
        Assert.assertThrows(IllegalArgumentException.class,
                () -> postServiceValidator.validatePostDto(emptyAuthorIdIsZeroPostDto));

        long projectId = 0L;
        PostCreateRequestDto emptyProjectIdIsZeroPostDto = PostCreateRequestDto.builder()
                .projectId(0L)
                .content("test")
                .build();
        Mockito.when(projectServiceClient.getProject(projectId)).thenReturn(projectDto);

        Assert.assertThrows(IllegalArgumentException.class,
                () -> postServiceValidator.validatePostDto(emptyProjectIdIsZeroPostDto));

        Mockito.when(userServiceClient.getUser(111L)).thenReturn(userDto);
        Mockito.when(projectServiceClient.getProject(222L)).thenReturn(projectDto);

        postServiceValidator.validatePostDto(validPostCreateRequestDto);
    }

    @Test
    @DisplayName("Test author and project exist for post")
    void testValidateAuthorAndProjectPostDto() {
        Mockito.when(userServiceClient.getUser(11111111L))
                .thenReturn(new UserResponseDto(1L, null, null, null, null));
        Mockito.when(projectServiceClient.getProject(222222222L))
                .thenReturn(new ProjectResponseDto(0L, null, null, null, null));
        PostCreateRequestDto unknownAuthorPostDto = PostCreateRequestDto.builder()
                .authorId(11111111L)
                .content("test")
                .build();

        Assert.assertThrows(IllegalArgumentException.class,
                () -> postServiceValidator.validatePostDto(unknownAuthorPostDto));

        PostCreateRequestDto unknownProjectPostDto = PostCreateRequestDto.builder()
                .projectId(222222222L)
                .content("test")
                .build();
        Assert.assertThrows(IllegalArgumentException.class,
                () -> postServiceValidator.validatePostDto(unknownProjectPostDto));
    }

    @Test
    void testValidatePostBeforePublish() {
        Post post = Post.builder()
                .id(123L)
                .content("some content")
                .authorId(222L)
                .published(true)
                .build();
        Assert.assertThrows(IllegalArgumentException.class,
                () -> postServiceValidator.validatePostBeforePublish(post));
    }

    @Test
    void testValidatePostBeforeUpdate() {

        Assert.assertThrows(IllegalArgumentException.class,
                () -> postServiceValidator.validatePostBeforeUpdate(null, post));

        Assert.assertThrows(IllegalArgumentException.class,
                () -> postServiceValidator.validatePostBeforeUpdate(post, postByOtherAuthor));

        Assert.assertThrows(IllegalArgumentException.class,
                () -> postServiceValidator.validatePostBeforeUpdate(post, postInOtherProject));

        postServiceValidator.validatePostBeforeUpdate(post, post);


    }
}