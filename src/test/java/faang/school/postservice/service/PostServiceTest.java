package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
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
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapperImpl postMapper;

    @Mock
    private PostValidator postValidator;

    @InjectMocks
    private PostService postService;

    private PostDto postDto = new PostDto();
    private Post post;

    @BeforeEach
    public void init() {
        postDto.setContent("content");
        postDto.setAuthorId(1L);
        postDto.setProjectId(2L);
        post = Post.builder()
                .id(1L)
                .content("another content")
                .authorId(10L)
                .projectId(20L)
                .published(false)
                .deleted(false)
                .build();
    }

    @Test
    public void testCreateDraftSuccess() {
        postService.createPostDraft(postDto);
        Post post = postMapper.toEntity(postDto);
        Post savedPost = postRepository.save(post);
        Assertions.assertSame(post, savedPost);
    }

    @Test
    public void testPublishPostSuccess() {
        Mockito.when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        postService.publishPost(1L);
        Assertions.assertTrue(post.isPublished());
    }

    @Test
    public void testUpdatePostSuccess() {
        PostDto updatedDto = new PostDto();
        updatedDto.setContent("updated content");
        Mockito.when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        postService.updatePost(1L, updatedDto);
        Assertions.assertSame("updated content", post.getContent());
    }

    @Test
    public void testDeletePostSuccess() {
        Mockito.when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        postService.deletePost(1L);
        Assertions.assertTrue(post.isDeleted());
    }

    @Test
    public void testGetPostByIdSuccess() {
        Mockito.when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        postService.getPost(1L);
        Assertions.assertSame(post, postService.getPost(1L));
    }

    // не проходят тесты при получении списка черновиков/постов,
    // при это при дебаге сервис отдает верное значение
    @Test
    public void testGetAuthorDraftsSuccess() {
        LocalDateTime createdAt1 = LocalDateTime.of(2024, Month.JANUARY, 28, 1, 1, 1);
        LocalDateTime createdAt2 = LocalDateTime.of(2024, Month.JANUARY, 28, 1, 1, 2);
        LocalDateTime createdAt3 = LocalDateTime.of(2024, Month.JANUARY, 28, 1, 1, 3);
        Post post1 = Post.builder()
                .id(1L)
                .authorId(1L)
                .content("1")
                .deleted(true)
                .published(true)
                .createdAt(createdAt1)
                .build();
        Post post2 = Post.builder()
                .id(2L)
                .authorId(1L)
                .content("2")
                .createdAt(createdAt2)
                .deleted(false)
                .published(false).build();
        Post post3 = Post.builder()
                .id(3L)
                .authorId(1L)
                .content("3")
                .createdAt(createdAt3)
                .deleted(false)
                .published(false)
                .build();
        List<Post> posts = List.of(post1, post2, post3);
        Mockito.when(postRepository.findByAuthorId(1L)).thenReturn(posts);
        List<PostDto> postDtos = postService.getAuthorDrafts(1L);
        Assertions.assertEquals(2, postDtos.size());
    }

    @Test
    public void testGetProjectDraftsSuccess() {
        LocalDateTime createdAt1 = LocalDateTime.of(2024, Month.JANUARY, 28, 1, 1, 1);
        LocalDateTime createdAt2 = LocalDateTime.of(2024, Month.JANUARY, 28, 1, 1, 2);
        LocalDateTime createdAt3 = LocalDateTime.of(2024, Month.JANUARY, 28, 1, 1, 3);
        Post post1 = Post.builder()
                .id(1L)
                .projectId(1L)
                .content("1")
                .deleted(true)
                .published(true)
                .createdAt(createdAt1)
                .build();
        Post post2 = Post.builder()
                .id(2L)
                .projectId(1L)
                .content("2")
                .createdAt(createdAt2)
                .deleted(false)
                .published(false).build();
        Post post3 = Post.builder()
                .id(3L)
                .projectId(1L)
                .content("3")
                .createdAt(createdAt3)
                .deleted(false)
                .published(false)
                .build();
        List<Post> posts = List.of(post1, post2, post3);
        Mockito.when(postRepository.findByProjectId(1L)).thenReturn(posts);
        List<PostDto> postDtos = postService.getProjectDrafts(1L);
        Assertions.assertEquals(2, postDtos.size());
    }

    @Test
    public void testGetAuthorPostsSuccess() {
        LocalDateTime createdAt1 = LocalDateTime.of(2024, Month.JANUARY, 28, 1, 1, 1);
        LocalDateTime createdAt2 = LocalDateTime.of(2024, Month.JANUARY, 28, 1, 1, 2);
        LocalDateTime createdAt3 = LocalDateTime.of(2024, Month.JANUARY, 28, 1, 1, 3);
        Post post1 = Post.builder()
                .id(1L)
                .authorId(1L)
                .content("1")
                .deleted(true)
                .published(true)
                .createdAt(createdAt1)
                .build();
        Post post2 = Post.builder()
                .id(2L)
                .authorId(1L)
                .content("2")
                .createdAt(createdAt2)
                .deleted(false)
                .published(true)
                .build();
        Post post3 = Post.builder()
                .id(3L)
                .authorId(1L)
                .content("3")
                .createdAt(createdAt3)
                .deleted(false)
                .published(false)
                .build();
        List<Post> posts = List.of(post1, post2, post3);
        Mockito.when(postRepository.findByAuthorId(1L)).thenReturn(posts);
        List<PostDto> postDtos = postService.getAuthorDrafts(1L);
        Assertions.assertEquals(1, postDtos.size());
    }

    @Test
    public void testGetProjectPostsSuccess() {
        LocalDateTime createdAt1 = LocalDateTime.of(2024, Month.JANUARY, 28, 1, 1, 1);
        LocalDateTime createdAt2 = LocalDateTime.of(2024, Month.JANUARY, 28, 1, 1, 2);
        LocalDateTime createdAt3 = LocalDateTime.of(2024, Month.JANUARY, 28, 1, 1, 3);
        Post post1 = Post.builder()
                .id(1L)
                .projectId(1L)
                .content("1")
                .deleted(true)
                .published(true)
                .createdAt(createdAt1)
                .build();
        Post post2 = Post.builder()
                .id(2L)
                .projectId(1L)
                .content("2")
                .createdAt(createdAt2)
                .deleted(false)
                .published(true)
                .build();
        Post post3 = Post.builder()
                .id(3L)
                .projectId(1L)
                .content("3")
                .createdAt(createdAt3)
                .deleted(false)
                .published(false)
                .build();
        List<Post> posts = List.of(post1, post2, post3);
        Mockito.when(postRepository.findByProjectId(1L)).thenReturn(posts);
        List<PostDto> postDtos = postService.getProjectPosts(1L);
        Assertions.assertEquals(1, postDtos.size());
    }


}
