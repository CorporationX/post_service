package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import liquibase.hub.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PostServiceTest {
    @InjectMocks
    private PostService postService;
    @Mock
    private PostRepository postRepository;
    @Spy
    private PostMapper postMapper = new PostMapperImpl();
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectServiceClient projectServiceClient;
    private PostDto postDto;
    private PostDto postDtoForProject;
    private UserDto userDto;
    private Post post;
    private Post postForProject;
    private ProjectDto projectDto;

    @BeforeEach
    public void setUp() {
        userDto = UserDto
                .builder()
                .id(1L)
                .username("Ali Baysarov")
                .email("alibajsarov353@gmail.com")
                .build();
        projectDto = ProjectDto
                .builder()
                .id(1L)
                .title("Proejct one")
                .build();
        post = Post
                .builder()
                .id(1L)
                .content("Hello")
                .authorId(1L)
                .published(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
        postForProject = Post
                .builder()
                .id(2L)
                .content("Hello")
                .projectId(1L)
                .published(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deleted(false)
                .build();
        postDto = PostDto
                .builder()
                .authorId(1L)
                .content("Hello")
                .projectId(1L)
                .published(false)
                .build();
        postDtoForProject = PostDto
                .builder()
                .id(2L)
                .published(false)
                .projectId(projectDto.getId())
                .content("Hello 2")
                .build();
    }

    @Test
    public void testCreateDraftForUser() {
        when(userServiceClient.getUser(postDto.getAuthorId())).thenReturn(userDto);
        when(postMapper.toEntity(postDto)).thenReturn(post);
        when(postMapper.toDto(post)).thenReturn(postDto);
        when(postRepository.save(post)).thenReturn(post);

        PostDto result = postService.createDraft(postDto);

        assertEquals(result, postDto);
    }

    @Test
    public void testCreateDraftForUserThrowsNotFoundException() {
        when(userServiceClient.getUser(postDto.getAuthorId())).thenReturn(null);
        DataValidationException ex = assertThrows(DataValidationException.class, () -> postService.createDraft(postDto));
        assertEquals("User not found", ex.getMessage());
        verify(postRepository, times(0)).save(post);
    }

    @Test
    public void testCreateDraftForProject() {
        when(projectServiceClient.getProject(postForProject.getProjectId()))
                .thenReturn(projectDto);
        when(postMapper.toEntity(postDtoForProject)).thenReturn(postForProject);
        when(postMapper.toDto(postForProject)).thenReturn(postDtoForProject);
        when(postRepository.save(postForProject)).thenReturn(postForProject);

        PostDto result = postService.createDraft(postDtoForProject);

        assertEquals(result, postDtoForProject);
    }

    @Test
    public void testCreateDraftForProjectThrowsNotFoundException() {
        when(projectServiceClient.getProject(postDtoForProject.getProjectId())).thenReturn(null);
        DataValidationException ex = assertThrows(DataValidationException.class, () -> postService
                .createDraft(postDtoForProject));
        assertEquals("Project not found", ex.getMessage());
        verify(postRepository, times(0)).save(postForProject);
    }

    @Test
    public void testPublishDraft() {
        when(postRepository.findReadyToPublish()).thenReturn(getPosts());
        PostDto result = postService.publishDraft(1L);
        assertNotNull(result);
        assertTrue(result.isPublished());
    }

    @Test
    public void testUpdatePost() {
        UpdatePostDto updateData = UpdatePostDto
                .builder()
                .content("New Post data")
                .build();
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        PostDto result = postService.updatePost(1L, updateData);
        assertNotNull(result);
        assertEquals(updateData.getContent(), result.getContent());
    }

    @Test
    public void testUpdatePostThrowsNotFoundError() {
        UpdatePostDto updateData = UpdatePostDto
                .builder()
                .content("New Post data")
                .build();
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> postService.updatePost(1L, updateData));
        assertEquals("Post not found", ex.getMessage());
    }

    @Test
    public void testDeletePost() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        postService.delete(1L);
        verify(postRepository).save(post);
        assertTrue(post.isDeleted());
    }

    @Test
    public void testGetOnePost() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postMapper.toDto(post)).thenReturn(postDto);
        PostDto result = postService.getPostById(1L);
        assertEquals(result, postDto);
    }

    @Test
    public void testGetDraftsByUser() {
        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(postRepository.findByAuthorId(userDto.getId())).thenReturn(getDraftPosts());
        List<PostDto> result = postService.getDraftsByUser(userDto.getId());
        assertNotNull(result);
        assertEquals(1, result.size());
        assertFalse(result.get(0).isPublished());
    }

    @Test
    public void testGetDraftsByProject() {
        when(projectServiceClient.getProject(1L)).thenReturn(projectDto);
        when(postRepository.findByProjectId(projectDto.getId())).thenReturn(getDraftPostsByProject());
        List<PostDto> result = postService.getDraftsByProject(projectDto.getId());
        assertNotNull(result);
        assertEquals(1, result.size());
        assertFalse(result.get(0).isPublished());
    }
    @Test
    public void testGetPublishedByUser() {
        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(postRepository.findByAuthorId(userDto.getId())).thenReturn(getDraftPosts());
        List<PostDto> result = postService.getPublishedByUser(1L);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isPublished());
    }
    @Test
    public void testGetPublishedByProject() {
        when(projectServiceClient.getProject(1L)).thenReturn(projectDto);
        when(postRepository.findByProjectId(projectDto.getId())).thenReturn(getDraftPostsByProject());
        List<PostDto> result = postService.getPublishedByProject(1L);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isPublished());
    }

    private List<Post> getDraftPostsByProject() {
        Post firstDraft = Post
                .builder()
                .id(1L)
                .projectId(1L)
                .content("Hello")
                .published(true)
                .build();
        Post secondDraft = Post
                .builder()
                .id(1L)
                .projectId(1L)
                .content("Hello")
                .published(false)
                .build();
        return Arrays.asList(firstDraft, secondDraft);
    }

    private List<Post> getDraftPosts() {
        Post firstDraft = Post
                .builder()
                .id(1L)
                .authorId(1L)
                .content("Hello")
                .published(true)
                .build();
        Post secondDraft = Post
                .builder()
                .id(1L)
                .authorId(1L)
                .content("Hello")
                .published(false)
                .build();
        return Arrays.asList(firstDraft, secondDraft);
    }

    private List<Post> getPosts() {
        List<Post> res = new ArrayList<>();
        res.add(post);
        return res;
    }
}
