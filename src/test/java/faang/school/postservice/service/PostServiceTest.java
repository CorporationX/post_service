package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @InjectMocks
    private PostService postService;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectServiceClient projectServiceClient;
    @Spy
    private PostMapper postMapper = Mappers.getMapper(PostMapper.class);
    @Mock
    private PostRepository postRepository;
    @Captor
    private ArgumentCaptor<Post> captor;
    private PostDto postDto;
    private Post post1;
    private Post post2;
    private Post post3;


    @BeforeEach
    void setUp() {
        post1 = Post.builder()
                .id(5L)
                .content("Hello")
                .authorId(1L)
                .published(false)
                .publishedAt(LocalDateTime.now())
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .build();
        post2 = Post.builder()
                .id(6L)
                .content("Hello")
                .authorId(1L)
                .published(true)
                .publishedAt(LocalDateTime.now())
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .build();
        post3 = Post.builder()
                .id(7)
                .authorId(1L)
                .published(false)
                .publishedAt(LocalDateTime.now())
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .build();
        postDto = PostDto.builder()
                .content("Hello, Java!")
                .build();
    }

    @Test
    void testCreateDraftNullAuthorAndProjectException() {
        DataValidationException exception = assertThrows(DataValidationException.class, () -> postService.createDraft(postDto));
        assertEquals("The author of the post is not specified", exception.getMessage());
    }

    @Test
    void testPublishFailed() {
        long id = 1;
        DataValidationException exception = assertThrows(DataValidationException.class, () -> postService.publish(id));
        assertEquals("Post with id " + id + " not found.", exception.getMessage());
    }

    @Test
    void testPublishIfIsPublish() {
        when(postRepository.findById(6L)).thenReturn(Optional.of(post2));
        DataValidationException exception = assertThrows(DataValidationException.class, () -> postService.publish(6));
        assertEquals("The post has already been published", exception.getMessage());
    }


    @Test
    void testUpdateSuccessful() {
        postDto.setId(5L);
        when(postRepository.findById(5L)).thenReturn(Optional.of(post1));
        postService.update(postDto);
        assertEquals(postDto.getContent(), post1.getContent());
        Mockito.verify(postRepository).save(post1);
    }

    @Test
    void testDeletePostSuccessful() {
        when(postRepository.findById(6L)).thenReturn(Optional.of(post2));
        postService.deletePost(6);
        assertFalse(post2.isPublished());
        assertTrue(post2.isDeleted());
    }

    @Test
    void testGetPostByIdSuccessful() {
        when(postRepository.findById(6L)).thenReturn(Optional.ofNullable(post2));
        PostDto postDtoNew = postService.getPostById(6);
        assertEquals(post2.getContent(), postDtoNew.getContent());
        assertEquals(post2.getAuthorId(), postDtoNew.getAuthorId());
    }

    @Test
    void testGetDraftsByAuthorIdSuccessful() {
        when(postRepository.findByAuthorId(1)).thenReturn(Arrays.asList(post1, post2, post3));
        List<PostDto> postsDto = postService.getDraftsByAuthorId(1);
        assertEquals(2, postsDto.size());
    }

    @Test
    void testGetDraftsByAuthorIdFailed() {
        post1.setCreatedAt(null);
        post3.setCreatedAt(null);
        when(postRepository.findByAuthorId(1)).thenReturn(Arrays.asList(post1, post3));
        DataValidationException exception = assertThrows(DataValidationException.class, () -> postService.getDraftsByAuthorId(1));
        assertEquals("Invalid date", exception.getMessage());
    }

    @Test
    void testGetPublishedPostsByAuthorIdSuccessful() {
        post1.setPublished(true);
        when(postRepository.findByAuthorId(1)).thenReturn(Arrays.asList(post1, post2, post3));
        List<PostDto> postDtos = postService.getPublishedPostsByAuthorId(1);
        assertEquals(2, postDtos.size());
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

        PostDto actualDto = postService.update(postDto);

        assertEquals("New Content", actualDto.getContent());
        assertNotNull(actualDto.getUpdatedAt());
    }
}