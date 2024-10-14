package faang.school.postservice.service.post;

import faang.school.postservice.model.dto.post.PostDto;
import faang.school.postservice.mapper.post.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.impl.post.PostServiceImpl;
import faang.school.postservice.validator.post.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import faang.school.postservice.service.HashtagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTest {

    @InjectMocks
    private PostServiceImpl postService;

    @Mock
    private PostValidator postValidator;

    @Spy
    private PostMapperImpl postMapper;

    @Mock
    private PostRepository postRepository;

    @Mock
    private HashtagService hashtagService;

    @Mock
    private ModerationDictionary dictionary;

    @Mock
    private PostServiceAsyncImpl postServiceAsync;

    private PostDto examplePostDto;
    private Post examplePost;
    private LocalDateTime timeInstance;

    @BeforeEach
    void setUp() {
        timeInstance = LocalDateTime.now();

        examplePostDto = PostDto.builder()
                .id(1L)
                .authorId(1L)
                .content("Content")
                .publishedAt(timeInstance)
                .createdAt(timeInstance)
                .title("Title")
                .build();

        examplePost = Post.builder()
                .id(1L)
                .authorId(1L)
                .content("Content")
                .publishedAt(timeInstance)
                .createdAt(timeInstance)
                .title("Title")
                .verified(null)
                .verifiedDate(null)
                .build();

        ReflectionTestUtils.setField(postService, "batchSize", 1);
    }

    @Test
    void createDraftPost_shouldReturnPostDto() {
        // Arrange
        when(postMapper.toEntity(any(PostDto.class))).thenReturn(examplePost);
        when(postRepository.save(any(Post.class))).thenReturn(examplePost);

        // Act
        PostDto result = postService.createDraftPost(examplePostDto);

        // Assert
        assertEquals(examplePostDto, result);
        assertFalse(examplePost.isPublished());
        verify(postValidator, times(1)).createDraftPostValidator(examplePostDto);
        verify(postRepository, times(1)).save(examplePost);
    }

    @Test
    void publishPost_shouldReturnPostDto() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.ofNullable(examplePost));
        when(postRepository.save(any(Post.class))).thenReturn(examplePost);

        // Act
        postService.publishPost(examplePostDto);

        // Assert
        assertTrue(examplePost.isPublished());
        assertNotNull(examplePost.getPublishedAt());
        verify(postRepository, times(1)).findById(1L);
        verify(postValidator, times(1)).publishPostValidator(examplePost);
        verify(postRepository, times(1)).save(examplePost);
    }

    @Test
    void updatePost_shouldReturnPostDto() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.ofNullable(examplePost));
        when(postRepository.save(any(Post.class))).thenReturn(examplePost);

        // Act
        PostDto result = postService.updatePost(examplePostDto);

        // Assert
        assertEquals(examplePostDto, result);
        verify(postRepository, times(1)).findById(1L);
        verify(postValidator, times(1)).updatePostValidator(examplePost, examplePostDto);
        verify(postRepository, times(1)).save(examplePost);
    }

    @Test
    void softDeletePost_shouldReturnPostDto() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.ofNullable(examplePost));
        when(postRepository.save(any(Post.class))).thenReturn(examplePost);

        // Act
        PostDto result = postService.softDeletePost(1L);

        // Assert
        assertNotEquals(examplePostDto, result);
        assertFalse(examplePost.isPublished());
        assertTrue(examplePost.isDeleted());
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).save(examplePost);
    }

    @Test
    void getPost_shouldReturnPostDto() {
        // Arrange
        when(postRepository.findById(1L)).thenReturn(Optional.ofNullable(examplePost));

        // Act
        PostDto result = postService.getPost(1L);

        // Assert
        assertEquals(examplePostDto, result);
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    void getAllDraftsByAuthorId_shouldReturnListOfPostDto() {
        // Arrange
        examplePost = Post.builder()
                .id(1L)
                .authorId(1L)
                .content("Content")
                .title("Title")
                .createdAt(timeInstance)
                .publishedAt(timeInstance)
                .deleted(false)
                .published(false)
                .build();

        List<Post> iterablePostList = List.of(examplePost);
        when(postRepository.findAll()).thenReturn(iterablePostList);

        // Act
        List<PostDto> result = postService.getAllDraftsByAuthorId(1L);

        // Assert
        List<PostDto> expectedResult = List.of(examplePostDto);
        assertEquals(expectedResult, result);
        verify(postValidator, times(1)).validateIfAuthorExists(1L);
    }

    @Test
    void getAllDraftsByProjectId_shouldReturnListOfEventDto() {
        // Arrange
        examplePost = Post.builder()
                .id(1L)
                .projectId(1L)
                .content("Content")
                .title("Title")
                .createdAt(timeInstance)
                .deleted(false)
                .published(false)
                .build();

        examplePostDto = PostDto.builder()
                .id(1L)
                .projectId(1L)
                .content("Content")
                .createdAt(timeInstance)
                .title("Title")
                .build();

        List<Post> iterablePostList = List.of(examplePost);
        when(postRepository.findAll()).thenReturn(iterablePostList);

        // Act
        List<PostDto> result = postService.getAllDraftsByProjectId(1L);

        // Assert
        List<PostDto> expectedResult = List.of(examplePostDto);
        assertEquals(expectedResult, result);
        verify(postValidator, times(1)).validateIfProjectExists(1L);
    }

    @Test
    void getAllPublishedPostsByAuthorId_shouldReturnListOfPostDto() {
        // Arrange
        examplePost = Post.builder()
                .id(1L)
                .authorId(1L)
                .content("Content")
                .title("Title")
                .createdAt(timeInstance.minusMinutes(2))
                .publishedAt(timeInstance)
                .deleted(false)
                .published(true)
                .build();

        examplePostDto = PostDto.builder()
                .id(1L)
                .authorId(1L)
                .content("Content")
                .title("Title")
                .createdAt(timeInstance.minusMinutes(2))
                .publishedAt(timeInstance)
                .deleted(false)
                .published(true)
                .build();

        List<Post> iterablePostList = List.of(examplePost);
        when(postRepository.findAll()).thenReturn(iterablePostList);

        // Act
        List<PostDto> result = postService.getAllPublishedPostsByAuthorId(1L);

        // Assert
        List<PostDto> expectedResult = List.of(examplePostDto);
        assertEquals(expectedResult, result);
        verify(postValidator, times(1)).validateIfAuthorExists(1L);
    }

    @Test
    void getAllPublishedPostsByProjectId_shouldReturnListOfPostDto() {
        // Arrange
        examplePost = Post.builder()
                .id(1L)
                .projectId(1L)
                .content("Content")
                .title("Title")
                .createdAt(timeInstance.minusMinutes(2))
                .publishedAt(timeInstance)
                .deleted(false)
                .published(true)
                .build();

        examplePostDto = PostDto.builder()
                .id(1L)
                .projectId(1L)
                .content("Content")
                .title("Title")
                .createdAt(timeInstance.minusMinutes(2))
                .publishedAt(timeInstance)
                .deleted(false)
                .published(true)
                .build();

        List<Post> iterablePostList = List.of(examplePost);
        when(postRepository.findAll()).thenReturn(iterablePostList);

        // Act
        List<PostDto> result = postService.getAllPublishedPostsByProjectId(1L);

        // Assert
        List<PostDto> expectedResult = List.of(examplePostDto);
        assertEquals(expectedResult, result);
        verify(postValidator, times(1)).validateIfProjectExists(1L);
    }

    @Test
    void getPostsByHashtagOk() {
        PostDto post1 = PostDto.builder().publishedAt(LocalDateTime.now().minusDays(3)).build();
        PostDto post2 = PostDto.builder().publishedAt(LocalDateTime.now()).build();

        when(hashtagService.findPostsByHashtag(anyString())).thenReturn(List.of(post1, post2));

        List<PostDto> posts = postService.getPostsByHashtag("a");

        assertEquals(2, posts.size());
    }

    @Test
    @DisplayName("Publish Scheduled Posts Test")
    void testPublishScheduledPosts() {
        doReturn(List.of(examplePost)).when(postRepository).findReadyToPublish();

        postService.publishScheduledPosts(1000);

        verify(postRepository).findReadyToPublish();
        verify(postServiceAsync).publishScheduledPostsAsyncInBatch(anyList());
    }

    @Test
    void testModeratePosts(){
        Post badPost = Post.builder().content("here is bad word babushka").verified(null).verifiedDate(null).build();
        List<Post> posts = List.of(examplePost, badPost);

        when(postRepository.findAllByVerifiedDateIsNull()).thenReturn(posts);

        postService.moderatePosts();

        verify(postServiceAsync, times(2)).moderatePostsByBatches(any());
    }
}
