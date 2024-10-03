package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.PostException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.PostServiceImpl;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class PostServiceImplTest {
    @Mock
    private PostRepository postRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @Spy
    private PostMapper postMapper;

    @InjectMocks
    private PostServiceImpl postService;

    private Post post;
    private PostDto postDto;
    private List<Post> preparedPosts;

    @BeforeEach
    void setUp() {
        postDto = new PostDto();
        postDto.setId(1L);

        // 1 опубликован 3 (1 из них удалён) не опубликовано
        // 1 удалён 3 не удалено
        preparedPosts = new ArrayList<>();

        post = new Post();
        post.setId(1L);
        postDto.setAuthorId(1L);
        post.setPublished(false);
        post.setDeleted(false);
        post.setCreatedAt(LocalDateTime.now().plusDays(1));
        preparedPosts.add(post);

        Post post2 = new Post();
        post2.setId(2L);
        post2.setPublished(true);
        post2.setDeleted(false);
        post2.setCreatedAt(LocalDateTime.now().plusDays(2));
        preparedPosts.add(post2);

        Post post3 = new Post();
        post3.setId(3L);
        post3.setPublished(false);
        post3.setDeleted(false);
        post3.setCreatedAt(LocalDateTime.now().plusDays(3));
        preparedPosts.add(post3);

        Post post4 = new Post();
        post4.setId(3L);
        post4.setPublished(false);
        post4.setDeleted(true);
        post4.setCreatedAt(LocalDateTime.now().plusDays(4));
        preparedPosts.add(post4);
    }

    @Test
    void testPostCreatePost() {
        UserDto userDto = new UserDto(1L, "v", "@");
        ProjectDto projectDto = new ProjectDto();
        projectDto.setId(1L);

        when(postMapper.toEntity(postDto)).thenReturn(post);
        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(projectServiceClient.getProject(1L)).thenReturn(projectDto);

        postService.createPost(postDto);

        postDto.setAuthorId(null);
        postDto.setProjectId(1L);

        postService.createPost(postDto);

        verify(postRepository, times(2)).save(post);
    }

    @Test
    void testNonExistentAuthor() {
        when(userServiceClient.getUser(1L)).thenThrow(FeignException.class);

        assertThrows(FeignException.class, () -> postService.createPost(postDto));
    }

    @Test
    void testNonExistentProject() {
        when(userServiceClient.getUser(1L)).thenThrow(FeignException.class);

        assertThrows(FeignException.class, () -> postService.createPost(postDto));
    }

    @Test
    void testPublishPostNonExistentPost() {
        when(postRepository.findById(postDto.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.publishPost(2L));
    }

    @Test
    void testPublishPostPublishedPost() {
        post.setPublished(true);

        when(postRepository.findById(postDto.getId())).thenReturn(Optional.of(post));

        assertThrows(PostException.class, () -> postService.publishPost(postDto.getId()));
    }

    @Test
    void testPublishPostExistentPost() {
        when(postRepository.findById(postDto.getId())).thenReturn(Optional.of(post));

        postService.publishPost(postDto.getId());

        verify(postRepository, times(1)).save(post);
        verify(postMapper,times(1)).toDto(post);
    }

    @Test
    void testUpdatePostNonExistentPost() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.updatePost(postDto));
    }

    @Test
    void testUpdatePostExistentPost() {
        when(postRepository.findById(postDto.getId())).thenReturn(Optional.of(post));
        when(postMapper.toEntity(postDto)).thenReturn(post);

        postService.updatePost(postDto);

        verify(postRepository, times(1)).save(post);
    }

    @Test
    void testDeletePostNonExistentPost() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.deletePost(1L));
    }

    @Test
    void testDeletePostExistentPost() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postMapper.toDto(post)).thenReturn(postDto);

        postService.deletePost(1L);

        verify(postRepository, times(1)).save(post);
        verify(postMapper, times(1)).toDto(post);
    }

    @Test
    void testDeletePostDeletedPost() {
        post.setDeleted(true);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        assertThrows(PostException.class, () -> postService.deletePost(1L));
    }

    @Test
    void testGet() {
        Optional<Post> opt = Optional.of(post);

        when(postRepository.findById(1L)).thenReturn(opt);
        when(postMapper.toDto(post)).thenReturn(postDto);

        postService.getPost(1L);

        verify(postMapper, times(1)).toDto(post);
    }

    @Test
    void testGetNonExistentPost() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.getPost(1L));
    }

    @Test
    void testGetAll_NonPublished_ByAuthorId_WithPosts() {
        when(postRepository.findByAuthorId(1L)).thenReturn(preparedPosts);

        postService.getAllNonPublishedByAuthorId(1L);

        verify(postMapper, times(2)).toDto(any());
    }

    @Test
    void testGetAll_NonPublished_ByAuthorId_WithoutPosts() {
        when(postRepository.findByAuthorId(1L)).thenReturn(List.of());

        postService.getAllNonPublishedByAuthorId(1L);

        verify(postMapper, times(0)).toDto(any());
    }

    @Test
    void testGetAll_Published_ByAuthorId_WithPosts() {
        when(postRepository.findByAuthorId(1L)).thenReturn(preparedPosts);

        postService.getAllPublishedByAuthorId(1L);

        verify(postMapper, times(1)).toDto(any());
    }

    @Test
    void testGetAll_Published_ByAuthorId_WithoutPosts() {
        when(postRepository.findByAuthorId(1L)).thenReturn(List.of());

        postService.getAllPublishedByAuthorId(1L);

        verify(postMapper, times(0)).toDto(any());
    }

    @Test
    void testGetAll_NonPublished_ByProjectId_WithPosts() {
        when(postRepository.findByProjectId(1L)).thenReturn(preparedPosts);

        postService.getAllNonPublishedByProjectId(1L);

        verify(postMapper, times(2)).toDto(any());
    }

    @Test
    void testGetAll_NonPublished_ByProjectId_WithoutPosts() {
        when(postRepository.findByProjectId(1L)).thenReturn(List.of());

        postService.getAllNonPublishedByProjectId(1L);

        verify(postMapper, times(0)).toDto(any());
    }

    @Test
    void testGetAll_Published_ByProjectId_WithPosts() {
        when(postRepository.findByProjectId(1L)).thenReturn(preparedPosts);

        postService.getAllPublishedByProjectId(1L);

        verify(postMapper, times(1)).toDto(any());
    }

    @Test
    void testGetAll_Published_ByProjectId_WithoutPosts() {
        when(postRepository.findByProjectId(1L)).thenReturn(List.of());

        postService.getAllPublishedByProjectId(1L);

        verify(postMapper, times(0)).toDto(any());
    }
}