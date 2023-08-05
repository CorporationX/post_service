package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    private final Long USER_ID = 1L;
    private final Long PROJECT_ID = 1L;

    @Mock
    private PostRepository postRepository;
    @Spy
    private PostMapperImpl postMapperImpl;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectServiceClient projectServiceClient;
    @Spy
    private PostValidator postValidator;
    @InjectMocks
    private PostService postService;

    private UserDto userDto;
    private ProjectDto projectDto;
    private PostDto postWithAuthorIdDto;
    private PostDto postWithProjectIdDto;
    private PostDto postWithAuthorIdAndProjectIdDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder().id(USER_ID).build();
        projectDto = ProjectDto.builder().id(PROJECT_ID).build();
        postWithAuthorIdDto = PostDto.builder().authorId(userDto.getId()).projectId(null).content("content").build();
        postWithProjectIdDto = PostDto.builder().authorId(null).projectId(projectDto.getId()).content("content2").build();
        postWithAuthorIdAndProjectIdDto = PostDto.builder().authorId(userDto.getId()).projectId(projectDto.getId()).content("content3").build();

    }

    @Test
    void testCreatePostWithAuthorIdSuccess() {
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        Post post = postMapperImpl.toPost(postWithAuthorIdDto);
        when(postRepository.save(post)).thenReturn(post);
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        PostDto postDto = postService.createPost(postWithAuthorIdDto);
        verify(postValidator, Mockito.times(1)).validationOfPostCreation(postWithAuthorIdDto);
        assertEquals(postDto, postWithAuthorIdDto);
    }

    @Test
    void testCreatePostWithAuthorIdFail() {
        when(userServiceClient.getUser(USER_ID)).thenThrow(NullPointerException.class);
        Assertions.assertThrows(NullPointerException.class, () -> postService.createPost(postWithAuthorIdDto));
    }

    @Test
    void testCreatePostWithProjectIdSuccess() {
        when(projectServiceClient.getProject(PROJECT_ID)).thenReturn(projectDto);
        Post post = postMapperImpl.toPost(postWithProjectIdDto);
        when(postRepository.save(post)).thenReturn(post);
        when(projectServiceClient.getProject(PROJECT_ID)).thenReturn(projectDto);
        PostDto postDto = postService.createPost(postWithProjectIdDto);
        verify(postValidator, Mockito.times(1)).validationOfPostCreation(postWithProjectIdDto);
        assertEquals(postDto, postWithProjectIdDto);
    }

    @Test
    void testCreatePostWithProjectIdFail() {
        when(projectServiceClient.getProject(PROJECT_ID)).thenThrow(NullPointerException.class);
        Assertions.assertThrows(NullPointerException.class, () -> postService.createPost(postWithProjectIdDto));
    }

    @Test
    void testCreatePostWithAuthorIdAndProjectIdFail() {
        try {
            postService.createPost(postWithAuthorIdAndProjectIdDto);
            when(projectServiceClient.getProject(PROJECT_ID)).thenReturn(projectDto);
            when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        } catch (DataValidationException e) {
            assertEquals("Author and project cannot be specified at the same time", e.getMessage());
        }
    }

    @Test
    void testGetNotDeletedDraftsByAuthorIdSuccess() {
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        when(postRepository.findByAuthorId(userDto.getId())).thenReturn(List.of(Post.builder().authorId(userDto.getId()).build()));
        List<PostDto> posts = postService.getNotDeletedDraftsByAuthorId(userDto.getId());

        assertEquals(1, posts.size());
    }

    @Test
    void testGetNotDeletedDraftsByAuthorIdFailIfUserNotFound() {
        when(userServiceClient.getUser(USER_ID)).thenThrow(NullPointerException.class);
        assertThrows((NullPointerException.class), () -> postService.getNotDeletedDraftsByAuthorId(USER_ID));
    }

    @Test
    void testGetNotDeletedDraftsByAuthorIdFailIfNoDraftsFound() {
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        when(postRepository.findByAuthorId(USER_ID)).thenReturn(List.of());
        assertEquals(0, postService.getNotDeletedDraftsByAuthorId(USER_ID).size());
    }

    @Test
    void testGetNotDeletedDraftsByProjectIdSuccess() {
        when(projectServiceClient.getProject(PROJECT_ID)).thenReturn(projectDto);
        when(postRepository.findByProjectId(projectDto.getId())).thenReturn(List.of(Post.builder().projectId(projectDto.getId()).build()));
        List<PostDto> posts = postService.getNotDeletedDraftsByProjectId(projectDto.getId());

        assertEquals(1, posts.size());
    }

    @Test
    void testGetNotDeletedDraftsByProjectIdFailIfProjectNotFound() {
        when(projectServiceClient.getProject(PROJECT_ID)).thenThrow(NullPointerException.class);
        assertThrows((NullPointerException.class), () -> postService.getNotDeletedDraftsByProjectId(PROJECT_ID));
    }

    @Test
    void testGetNotDeletedDraftsByProjectIdFailIfNoDraftsFound() {
        when(projectServiceClient.getProject(PROJECT_ID)).thenReturn(projectDto);
        when(postRepository.findByProjectId(PROJECT_ID)).thenReturn(List.of());
        assertEquals(0, postService.getNotDeletedDraftsByProjectId(PROJECT_ID).size());
    }
}