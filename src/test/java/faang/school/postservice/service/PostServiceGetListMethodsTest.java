package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.client.ProjectDto;
import faang.school.postservice.dto.client.UserDto;
import faang.school.postservice.exception.IncorrectIdException;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceGetListMethodsTest {

    @Spy
    private PostMapperImpl postMapper;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserServiceClient userService;
    @Mock
    private ProjectServiceClient projectService;
    @InjectMocks
    private PostService postService;

    private final long CORRECT_ID = 1L;
    private final LocalDateTime TEST_TIME = LocalDateTime.now().truncatedTo(ChronoUnit.HOURS);
    private UserDto correctUserDto = UserDto.builder().build();
    private ProjectDto correctProjectDto = ProjectDto.builder().build();

    @Test
    void testGetUserDraftsWithoutUserInDB() {
        when(userService.getUser(CORRECT_ID)).thenThrow(FeignException.class);
        assertThrows(IncorrectIdException.class, () -> postService.getUserDrafts(CORRECT_ID));
    }

    @Test
    void testGetUserDraftsWithEmptyList() {
        when(userService.getUser(CORRECT_ID)).thenReturn(correctUserDto);
        when(postRepository.findByAuthorId(CORRECT_ID)).thenReturn(new ArrayList<>());

        List<PostDto> actualList = postService.getUserDrafts(CORRECT_ID);
        assertEquals(new ArrayList<>(), actualList);
    }

    @Test
    void testGetUserDrafts() {
        when(userService.getUser(CORRECT_ID)).thenReturn(correctUserDto);
        when(postRepository.findByAuthorId(CORRECT_ID)).thenReturn(getListOfPost());

        List<PostDto> actualUserDrafts = postService.getUserDrafts(CORRECT_ID);
        List<PostDto> expectedUserDrafts = getCorrectListOfDraftPostDto();
        assertEquals(expectedUserDrafts, actualUserDrafts);
    }

    @Test
    void testGetProjectDraftsWithoutProjectInDB() {
        when(projectService.getProject(CORRECT_ID)).thenThrow(FeignException.class);
        assertThrows(IncorrectIdException.class, () -> postService.getProjectDrafts(CORRECT_ID));
    }

    @Test
    void testGetProjectDraftsWithEmptyList() {
        when(projectService.getProject(CORRECT_ID)).thenReturn(correctProjectDto);
        when(postRepository.findByProjectId(CORRECT_ID)).thenReturn(new ArrayList<>());

        List<PostDto> actualList = postService.getProjectDrafts(CORRECT_ID);
        assertEquals(new ArrayList<>(), actualList);
    }

    @Test
    void testGetProjectDrafts() {
        when(projectService.getProject(CORRECT_ID)).thenReturn(correctProjectDto);
        when(postRepository.findByProjectId(CORRECT_ID)).thenReturn(getListOfPost());

        List<PostDto> actualProjectDrafts = postService.getProjectDrafts(CORRECT_ID);
        List<PostDto> expectedProjectDrafts = getCorrectListOfDraftPostDto();
        assertEquals(expectedProjectDrafts, actualProjectDrafts);
    }

    @Test
    void testGetUserPostsWithoutUserInDB() {
        when(userService.getUser(CORRECT_ID)).thenThrow(FeignException.class);
        assertThrows(IncorrectIdException.class, () -> postService.getUserPosts(CORRECT_ID));
    }

    @Test
    void testGetUserPostsWithEmptyList() {
        when(userService.getUser(CORRECT_ID)).thenReturn(correctUserDto);
        when(postRepository.findByAuthorId(CORRECT_ID)).thenReturn(new ArrayList<>());

        List<PostDto> actualList = postService.getUserPosts(CORRECT_ID);
        assertEquals(new ArrayList<>(), actualList);
    }

    @Test
    void testGetUserPosts() {
        when(userService.getUser(CORRECT_ID)).thenReturn(correctUserDto);
        when(postRepository.findByAuthorId(CORRECT_ID)).thenReturn(getListOfPost());

        List<PostDto> actualUserPosts = postService.getUserPosts(CORRECT_ID);
        List<PostDto> expectedUserPost = getCorrectListOfPostDto();
        assertEquals(expectedUserPost, actualUserPosts);
    }

    @Test
    void testGetProjectPostsWithoutProjectInDB() {
        when(projectService.getProject(CORRECT_ID)).thenThrow(FeignException.class);
        assertThrows(IncorrectIdException.class, () -> postService.getProjectPosts(CORRECT_ID));
    }

    @Test
    void testGetProjectPostsWithEmptyList() {
        when(projectService.getProject(CORRECT_ID)).thenReturn(correctProjectDto);
        when(postRepository.findByProjectId(CORRECT_ID)).thenReturn(new ArrayList<>());

        List<PostDto> actualList = postService.getProjectPosts(CORRECT_ID);
        assertEquals(new ArrayList<>(), actualList);
    }

    @Test
    void testGetProjectPosts() {
        when(projectService.getProject(CORRECT_ID)).thenReturn(correctProjectDto);
        when(postRepository.findByProjectId(CORRECT_ID)).thenReturn(getListOfPost());

        List<PostDto> actualProjectPosts = postService.getProjectPosts(CORRECT_ID);
        List<PostDto> expectedProjectPosts = getCorrectListOfPostDto();
        assertEquals(expectedProjectPosts, actualProjectPosts);
    }

    private List<PostDto> getCorrectListOfDraftPostDto() {
        PostDto dto1 = PostDto.builder()
                .createdAt(TEST_TIME.minusMonths(3))
                .build();
        PostDto dto2 = PostDto.builder()
                .createdAt(TEST_TIME.minusDays(5))
                .build();
        PostDto dto3 = PostDto.builder()
                .createdAt(TEST_TIME.minusMonths(5))
                .build();

        return List.of(dto2, dto1, dto3);
    }

    private List<PostDto> getCorrectListOfPostDto() {
        PostDto dto1 = PostDto.builder()
                .createdAt(TEST_TIME.minusMonths(3))
                .published(true)
                .build();
        PostDto dto2 = PostDto.builder()
                .createdAt(TEST_TIME.minusDays(5))
                .published(true)
                .build();
        PostDto dto3 = PostDto.builder()
                .createdAt(TEST_TIME.minusDays(10))
                .published(true)
                .build();

        return List.of(dto2, dto3, dto1);
    }

    private List<Post> getListOfPost() {
        Post post1 = Post.builder()
                .createdAt(TEST_TIME.minusMonths(3))
                .build();
        Post post2 = Post.builder()
                .createdAt(TEST_TIME.minusDays(5))
                .build();
        Post post3 = Post.builder()
                .createdAt(TEST_TIME)
                .deleted(true)
                .build();
        Post post4 = Post.builder()
                .createdAt(TEST_TIME.minusDays(10))
                .published(true)
                .build();
        Post post5 = Post.builder()
                .createdAt(TEST_TIME.minusMonths(5))
                .build();
        Post post6 = Post.builder()
                .createdAt(TEST_TIME.minusMonths(1))
                .published(true)
                .deleted(true)
                .build();
        Post post7 = Post.builder()
                .createdAt(TEST_TIME.minusMonths(3))
                .published(true)
                .build();
        Post post8 = Post.builder()
                .createdAt(TEST_TIME.minusDays(5))
                .published(true)
                .build();

        return List.of(post1, post2, post3, post4, post5, post6, post7, post8);
    }
}
