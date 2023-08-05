package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        verify(postValidator, Mockito.times(1)).validatePostContent(postWithAuthorIdDto);
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
        verify(postValidator, Mockito.times(1)).validatePostContent(postWithProjectIdDto);
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
    void testUpdatePostWithAuthorIdSuccess() {
        String content = "updated content";
        PostDto updatedPostDto = PostDto.builder().content(content).authorId(userDto.getId()).build();
        Post updatedPost = postMapperImpl.toPost(updatedPostDto);

        when(postRepository.findById(updatedPost.getId())).thenReturn(Optional.of(updatedPost));
        postService.updatePost(updatedPostDto);

        verify(postValidator, Mockito.times(1)).validationOfPostUpdate(updatedPostDto, updatedPost);
        verify(postRepository, Mockito.times(1)).save(updatedPost);

        assertEquals(content, updatedPost.getContent());
    }

    @Test
    void testUpdatePostWithAuthorIdFailIfPostNotFound() {
        Assertions.assertEquals("Post not found", Assertions.assertThrows(EntityNotFoundException.class, () -> postService.updatePost(postWithAuthorIdDto)).getMessage());
    }

    @Test
    void testUpdatePostWithProjectIdSuccess() {
        String content = "updated content";
        PostDto updatedPostDto = PostDto.builder().content(content).projectId(projectDto.getId()).build();
        Post updatedPost = postMapperImpl.toPost(updatedPostDto);

        when(postRepository.findById(updatedPost.getId())).thenReturn(Optional.of(updatedPost));
        postService.updatePost(updatedPostDto);

        verify(postValidator, Mockito.times(1)).validationOfPostUpdate(updatedPostDto, updatedPost);
        verify(postRepository, Mockito.times(1)).save(updatedPost);

        assertEquals(content, updatedPost.getContent());
    }

    @Test
    void testUpdatePostWithProjectIdFailIfPostNotFound() {
        Assertions.assertEquals("Post not found", Assertions.assertThrows(EntityNotFoundException.class, () -> postService.updatePost(postWithProjectIdDto)).getMessage());
    }

    @Test
    void testUpdatePostWithAuthorIdFailIfAuthorIdHasBeenChanged() {
        Long newAuthorId = 2L;
        PostDto updatedPostDto = PostDto.builder().content("updated content").authorId(newAuthorId).build();
        Post updatedPost = postMapperImpl.toPost(updatedPostDto);
        try {
            when(postRepository.findById(updatedPost.getId())).thenReturn(Optional.of(updatedPost));
            postService.updatePost(updatedPostDto);
            verify(postValidator, Mockito.times(1)).validationOfPostUpdate(updatedPostDto, updatedPost);
        } catch (DataValidationException e) {
            assertEquals("You cannot change the author of the post", e.getMessage());
        }
    }

    @Test
    void testUpdatePostWithProjectIdFailIfProjectIdHasBeenChanged() {
        Long newProjectId = 2L;
        PostDto updatedPostDto = PostDto.builder().content("updated content").projectId(newProjectId).build();
        Post updatedPost = postMapperImpl.toPost(updatedPostDto);
        try {
            when(postRepository.findById(updatedPost.getId())).thenReturn(Optional.of(updatedPost));
            postService.updatePost(updatedPostDto);
            verify(postValidator, Mockito.times(1)).validationOfPostUpdate(updatedPostDto, updatedPost);
        } catch (DataValidationException e) {
            assertEquals("You cannot change the project of the post", e.getMessage());
        }
    }
}