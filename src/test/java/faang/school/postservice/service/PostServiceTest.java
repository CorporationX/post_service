package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import faang.school.postservice.repository.PostRepository;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @InjectMocks
    private PostService postService;
    @Mock
    private PostRepository postRepository;
    @Spy
    private PostMapperImpl postMapper;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectServiceClient projectServiceClient;

    @Test
    void testCreateDraftPostValidData() {
        PostDto expectedDto = PostDto.builder()
                .id(1L)
                .content("Content")
                .authorId(1L)
                .build();
        Post post = Post.builder()
                .id(1L)
                .content("Content")
                .authorId(1L)
                .build();

        when(postRepository.save(post)).thenReturn(post);

        PostDto actualDto = postService.createDraftPost(expectedDto);

        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
        assertEquals(1L, actualDto.getAuthorId());
    }

    @Test
    void testCreateDraftPostValidateId() {
        PostDto postDto = PostDto.builder()
                .content("Content")
                .authorId(1L)
                .projectId(2L)
                .build();

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> postService.createDraftPost(postDto));
        assertEquals("Enter one thing: authorId or projectId", exception.getMessage());
    }

    @Test
    void testCreateDraftPostValidateUserExist() {
        PostDto postDto = PostDto.builder()
                .content("Content")
                .authorId(1L)
                .build();

        doThrow(FeignException.class).when(userServiceClient).getUser(1L);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> postService.createDraftPost(postDto));
        assertEquals("User with the specified authorId does not exist", exception.getMessage());
    }

    @Test
    void testCreateDraftPostValidateProjectExist() {
        PostDto postDto = PostDto.builder()
                .content("Content")
                .projectId(1L)
                .build();

        doThrow(FeignException.class).when(projectServiceClient).getProject(1L);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> postService.createDraftPost(postDto));
        assertEquals("Project with the specified projectId does not exist", exception.getMessage());
    }

    @Test
    void testPublishPostValidData() {
        long id = 1L;
        Post post = Post.builder()
                .id(id)
                .content("Content")
                .authorId(1L)
                .published(false)
                .deleted(false)
                .build();


        when(postRepository.findById(id)).thenReturn(Optional.of(post));

        PostDto actualDto = postService.publishPost(id);

        assertTrue(actualDto.isPublished());
        assertNotNull(actualDto.getPublishedAt());
    }

    @Test
    void testPublishPostValidExist() {
        long id = 1L;

        when(postRepository.findById(id)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> postService.publishPost(id));
        assertEquals("Post with the specified id does not exist", exception.getMessage());
    }

    @Test
    void testPublishPostValidNotPublished() {
        long id = 1L;
        Post post = Post.builder()
                .id(id)
                .content("Content")
                .authorId(1L)
                .published(true)
                .deleted(false)
                .build();


        when(postRepository.findById(id)).thenReturn(Optional.of(post));

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> postService.publishPost(id));
        assertEquals("Post is already published or deleted", exception.getMessage());
    }

    @Test
    void testUpdatePostValidData() {
        long id = 1L;
        PostDto postDto = PostDto.builder()
                .id(id)
                .content("New Content")
                .authorId(1L)
                .build();
        Post post = Post.builder()
                .id(id)
                .content("Content")
                .authorId(1L)
                .build();

        when(postRepository.findById(id)).thenReturn(Optional.of(post));

        PostDto actualDto = postService.updatePost(postDto);

        assertEquals("New Content", actualDto.getContent());
        assertNotNull(actualDto.getUpdatedAt());
    }

    @Test
    void testSoftDeletePostValidData() {
        long id = 1L;
        Post post = Post.builder()
                .id(id)
                .content("Content")
                .authorId(1L)
                .deleted(false)
                .build();

        when(postRepository.findById(id)).thenReturn(Optional.of(post));

        PostDto actualDto = postService.softDeletePost(id);

        assertTrue(actualDto.isDeleted());
        assertEquals(post.getUpdatedAt(), actualDto.getUpdatedAt());
    }

    @Test
    void testSoftDeletePostInvalidData() {
        long id = 1L;
        Post post = Post.builder()
                .id(id)
                .content("Content")
                .authorId(1L)
                .deleted(true)
                .build();

        when(postRepository.findById(id)).thenReturn(Optional.of(post));

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> postService.softDeletePost(id));
        assertEquals("Post is already deleted", exception.getMessage());
    }

    @Test
    void testGetPostByIdValidData() {
        long id = 1L;
        Post post = Post.builder()
                .id(id)
                .content("Content")
                .authorId(1L)
                .published(true)
                .build();

        when(postRepository.findById(id)).thenReturn(Optional.of(post));

        PostDto actualDto = postService.getPostById(id);

        assertNotNull(actualDto);
        assertEquals("Content", actualDto.getContent());
    }

    @Test
    void testGetPostByIdInvalidData() {
        long id = 1L;
        Post post = Post.builder()
                .id(id)
                .content("Content")
                .authorId(1L)
                .build();

        when(postRepository.findById(id)).thenReturn(Optional.of(post));

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> postService.getPostById(id));
        assertEquals("Post is not published", exception.getMessage());
    }

    @Test
    void testGetDraftPostByUserIdValidData() {
        LocalDateTime createdAt1 = LocalDateTime.of(2023, Month.AUGUST, 9, 1, 1, 1);
        LocalDateTime createdAt2 = LocalDateTime.of(2023, Month.AUGUST, 9, 1, 1, 2);
        Post post1 = Post.builder()
                .id(1L)
                .content("Valid1")
                .authorId(1L)
                .createdAt(createdAt1)
                .build();
        Post post2 = Post.builder()
                .id(2L)
                .content("Valid2")
                .authorId(1L)
                .createdAt(createdAt2)
                .build();
        Post post3 = Post.builder()
                .id(3L)
                .content("Invalid1")
                .authorId(1L)
                .deleted(true)
                .build();
        Post post4 = Post.builder()
                .id(4L)
                .content("Invalid2")
                .authorId(1L)
                .published(true)
                .build();
        List<Post> posts = List.of(post1, post2, post3, post4);

        when(postRepository.findByAuthorId(1L)).thenReturn(posts);

        List<PostDto> actualDto = postService.getDraftPostsByUserId(1L);

        assertEquals(2, actualDto.size());
        assertEquals(createdAt1, actualDto.get(1).getCreatedAt());
    }

    @Test
    void testGetDraftPostByUserIdInvalidData() {
        Post post1 = Post.builder()
                .id(1L)
                .content("Invalid1")
                .authorId(1L)
                .deleted(true)
                .build();
        Post post2 = Post.builder()
                .id(2L)
                .content("Invalid2")
                .authorId(1L)
                .published(true)
                .build();
        List<Post> posts = List.of(post1, post2);

        when(postRepository.findByAuthorId(1L)).thenReturn(posts);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> postService.getDraftPostsByUserId(1L));
        assertEquals("Draft post not found", exception.getMessage());
    }

    @Test
    void testGetDraftPostByProjectIdValidData() {
        LocalDateTime createdAt1 = LocalDateTime.of(2023, Month.AUGUST, 9, 1, 1, 1);
        LocalDateTime createdAt2 = LocalDateTime.of(2023, Month.AUGUST, 9, 1, 1, 2);
        Post post1 = Post.builder()
                .id(1L)
                .content("Valid1")
                .authorId(1L)
                .createdAt(createdAt1)
                .build();
        Post post2 = Post.builder()
                .id(2L)
                .content("Valid2")
                .authorId(1L)
                .createdAt(createdAt2)
                .build();
        Post post3 = Post.builder()
                .id(3L)
                .content("Invalid1")
                .authorId(1L)
                .deleted(true)
                .build();
        Post post4 = Post.builder()
                .id(4L)
                .content("Invalid2")
                .authorId(1L)
                .published(true)
                .build();
        List<Post> posts = List.of(post1, post2, post3, post4);

        when(postRepository.findByProjectId(1L)).thenReturn(posts);

        List<PostDto> actualDto = postService.getDraftPostsByProjectId(1L);

        assertEquals(2, actualDto.size());
        assertEquals(createdAt1, actualDto.get(1).getCreatedAt());
    }

    @Test
    void testGetDraftPostByProjectIdInvalidData() {
        Post post1 = Post.builder()
                .id(1L)
                .content("Invalid1")
                .authorId(1L)
                .deleted(true)
                .build();
        Post post2 = Post.builder()
                .id(2L)
                .content("Invalid2")
                .authorId(1L)
                .published(true)
                .build();
        List<Post> posts = List.of(post1, post2);

        when(postRepository.findByProjectId(1L)).thenReturn(posts);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> postService.getDraftPostsByProjectId(1L));
        assertEquals("Draft post not found", exception.getMessage());
    }
}
