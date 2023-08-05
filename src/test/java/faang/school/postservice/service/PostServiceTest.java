package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ad.AdRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private AdRepository adRepository;
    @Mock
    private PostRepository postRepository;
    @Spy
    private PostMapperImpl postMapper;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectServiceClient projectServiceClient;
    @InjectMocks
    private PostService postService;
    private Post postOne;
    private Post postTwo;
    private Post postTree;

    private PostDto postDtoOne;
    private PostDto postDtoTwo;
    private PostDto postDtoTree;
    List<PostDto> posts;

    private UpdatePostDto updatePostDto;


    @BeforeEach
    void setUp() {
        posts = new ArrayList<>();
        List<Comment> comments = new ArrayList<>(List.of(Comment.builder().id(1L).build()));
        List<Like> likes = new ArrayList<>(List.of(Like.builder().id(1L).build()));
        postOne = Post.builder().id(1L)
                .createdAt(LocalDateTime.of(2022, 3, 1, 0, 0))
                .deleted(false).published(true).build();
        postTwo = Post.builder().id(2L)
                .createdAt(LocalDateTime.of(2022, 1, 1, 0, 0))
                .deleted(false).published(false).build();
        postTree = Post.builder().id(3L)
                .createdAt(LocalDateTime.of(2022, 2, 1, 0, 0))
                .deleted(false).published(true).build();
        postDtoOne = PostDto.builder().id(1L).createdAt(LocalDateTime.of(2022, 3, 1, 0, 0))
                .deleted(false).published(true).build();
        postDtoTwo = PostDto.builder().id(2L).createdAt(LocalDateTime.of(2022, 1, 1, 0, 0))
                .deleted(false).published(false).build();
        postDtoTree = PostDto.builder().id(3L).createdAt(LocalDateTime.of(2022, 2, 1, 0, 0))
                .deleted(false).published(true).build();
        updatePostDto = UpdatePostDto.builder().content("content").build();
        posts.add(postDtoOne);
        posts.add(postDtoTree);

    }

    @Test
    void testCreatePostDataValidationException() {
        CreatePostDto createPostDto = CreatePostDto.builder().authorId(1L).projectId(1L).build();
        assertThrows(DataValidationException.class, () -> postService.createPost(createPostDto));
    }

    @Test
    void testCreatePostMockAuthorDataValidationException() {
        CreatePostDto createPostDto = CreatePostDto.builder().authorId(1L).projectId(null).build();
        when(userServiceClient.getUser(1L)).thenReturn(null);
        assertThrows(DataValidationException.class, () -> postService.createPost(createPostDto));
    }

    @Test
    void testCreatePostMockProjectDataValidationException() {
        CreatePostDto createPostDto = CreatePostDto.builder().authorId(null).projectId(1L).build();
        when(projectServiceClient.getProject(1L)).thenReturn(null);
        assertThrows(DataValidationException.class, () -> postService.createPost(createPostDto));
    }

    @Test
    void testCreatePost() {
        Post post = Post.builder()
                .authorId(null).projectId(1L)
                .deleted(false).published(false).build();
        CreatePostDto createPostDto = CreatePostDto.builder().authorId(null).projectId(1L).build();
        when(projectServiceClient.getProject(1L)).thenReturn(new ProjectDto());
        postService.createPost(createPostDto);
        verify(postRepository).save(post);
    }

    @Test
    void testPublishPost() {

    }

    @Test
    void testUpdatePostDataValidationException() {
        when(postMapper.toDto(any())).thenThrow(new RuntimeException());
        when(postRepository.findById(any())).thenThrow(new RuntimeException());
        when(adRepository.findById(any())).thenThrow(new RuntimeException());
        when(postRepository.save(any())).thenThrow(new RuntimeException());
        assertThrows(DataValidationException.class, () -> postService.updatePost(updatePostDto));
    }

    @Test
    void softDeletePost() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(postOne));
        when(postRepository.save(postOne)).thenReturn(postOne);
        assertEquals(postService.softDeletePost(1L), postOne);
    }

    @Test
    void getPostById() {
    }

    @Test
    void getAllPostsByAuthorId() {
        when(postRepository.findByProjectId(1L)).thenReturn(List.of(postOne, postTwo, postTree));
        when(postMapper.toDto(postTwo)).thenReturn(postDtoTwo);
        assertEquals(List.of(postDtoTwo), postService.getAllPostsByProjectId(1L));
    }

    @Test
    void getAllPostsByProjectId() {
        when(postRepository.findByProjectId(1L)).thenReturn(List.of(postOne, postTwo, postTree));
        when(postMapper.toDto(postTwo)).thenReturn(postDtoTwo);
        assertEquals(List.of(postDtoTwo), postService.getAllPostsByProjectId(1L));
    }

    @Test
    void getAllPostsByAuthorIdAndPublished() {
        when(postRepository.findByAuthorId(1L)).thenReturn(List.of(postOne, postTwo, postTree));
        when(postMapper.toDto(postOne)).thenReturn(postDtoOne);
        when(postMapper.toDto(postTree)).thenReturn(postDtoTree);
        assertEquals(posts, postService.getAllPostsByAuthorIdAndPublished(1L));
    }

    @Test
    void getAllPostsByProjectIdAndPublished() {
        when(postRepository.findByProjectId(1L)).thenReturn(List.of(postOne, postTwo, postTree));
        when(postMapper.toDto(postOne)).thenReturn(postDtoOne);
        when(postMapper.toDto(postTree)).thenReturn(postDtoTree);
        assertEquals(posts, postService.getAllPostsByProjectIdAndPublished(1L));

    }
}