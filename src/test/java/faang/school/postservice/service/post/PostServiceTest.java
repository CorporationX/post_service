package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.filter.FilterDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.filter.post.DeletedFilter;
import faang.school.postservice.filter.post.PostFilter;
import faang.school.postservice.filter.post.PublishedFilter;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.RedisMessagePublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import faang.school.postservice.sort.PostField;
import faang.school.postservice.sort.SortBy;
import faang.school.postservice.sort.SortByCreatedAt;
import faang.school.postservice.sort.SortByPublishedAt;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    private PostService postService;

    @Mock
    private PostRepository postRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Spy
    private ProjectServiceClient projectServiceClient;

    @Mock
    private RedisMessagePublisher redisMessagePublisher;

    @Spy
    private PostMapperImpl postMapper;

    private List<PostFilter> postFilters;

    private List<SortBy> sort;

    private Post post = new Post();

    private PostDto postDto = new PostDto();
    private FilterDto filterDto = new FilterDto();

    @BeforeEach
    void setUp() {
        postDto.setContent("test");

        postFilters = new ArrayList<>();
        postFilters.add(new DeletedFilter());
        postFilters.add(new PublishedFilter());

        sort = new ArrayList<>();
        sort.add(new SortByCreatedAt());
        sort.add(new SortByPublishedAt());

        postService = new PostService(
                redisMessagePublisher,
                postRepository,
                postMapper,
                postFilters,
                userServiceClient,
                projectServiceClient,
                sort
        );
    }

    @Captor
    private ArgumentCaptor<Post> postCaptor;

    @Test
    void testPostWithAuthorAndProject() {
        postDto.setAuthorId(1L);
        postDto.setProjectId(2L);

        assertThrows(IllegalStateException.class,
                () -> postService.createPost(postDto));
    }

    @Test
    void testUnExistUser() {
        postDto.setAuthorId(1L);
        when(userServiceClient.getUserById(1)).thenThrow(FeignException.class);

        assertThrows(FeignException.class,
                () -> postService.createPost(postDto));
    }

    @Test
    void testUnExistProject() {
        postDto.setProjectId(1L);
        when(projectServiceClient.getProject(1)).thenThrow(FeignException.class);

        assertThrows(FeignException.class,
                () -> postService.createPost(postDto));
    }

    @Test
    void testCreateSuccessful() {
        postDto.setAuthorId(1L);

        PostDto result = postService.createPost(postDto);
        Mockito.verify(postMapper).toEntity(postDto);
        Mockito.verify(postRepository).save(postCaptor.capture());
        Post savedPost = postCaptor.getValue();

        assertEquals(savedPost.getAuthorId(), postDto.getAuthorId());
        assertFalse(savedPost.isDeleted());
        assertFalse(savedPost.isPublished());
        assertNotNull(savedPost.getCreatedAt());

        assertEquals(result.getContent(), postDto.getContent());
    }

    @Test
    void testPostWasAlreadyPosted() {
        post.setPublished(true);
        when(postRepository.findById(4L)).thenReturn(Optional.of(post));

        assertThrows(IllegalStateException.class,
                () -> postService.publishPost(4));
    }

    @Test
    void testPostNotExistForPublishPost() {
        post.setPublished(true);
        when(postRepository.findById(-4L)).thenThrow(IllegalStateException.class);

        assertThrows(IllegalStateException.class,
                () -> postService.publishPost(-4));
    }

    @Test
    void testPostNotExistForUpdatePost() {
        when(postRepository.findById(-4L)).thenThrow(IllegalStateException.class);

        assertThrows(IllegalStateException.class,
                () -> postService.updatePost(-4, postDto));
    }

    @Test
    void testSuccessfulPublishPost() {
        post.setPublished(false);
        when(postRepository.findById(4L)).thenReturn(Optional.of(post));

        postService.publishPost(4L);
        Mockito.verify(postRepository).save(postCaptor.capture());

        Post postToSave = postCaptor.getValue();

        assertNotNull(postToSave.getPublishedAt());
        assertNotNull(postToSave.getUpdatedAt());
        assertEquals(postToSave.getUpdatedAt(), postToSave.getPublishedAt());
        assertTrue(postToSave.isPublished());
    }

    @Test
    void testSuccessfulUpdatePost() {
        post.setContent("Old text");
        when(postRepository.findById(4L)).thenReturn(Optional.of(post));

        PostDto result = postService.updatePost(4, postDto);
        Mockito.verify(postRepository).save(postCaptor.capture());

        Post postResult = postCaptor.getValue();
        assertEquals(postResult.getContent(), postDto.getContent());
        assertEquals(result.getContent(), postDto.getContent());
    }

    @Test
    void testPostNotExistForGetPostDto() {
        when(postRepository.findById(-4L)).thenThrow(IllegalStateException.class);

        assertThrows(IllegalStateException.class,
                () -> postService.getPostDto(-4));
    }

    @Test
    void testSuccessfulGetPostDto() {
        post.setId(4L);
        post.setContent("test content");
        when(postRepository.findById(4L)).thenReturn(Optional.of(post));

        PostDto result = postService.getPostDto(4);
        assertEquals(result.getContent(), post.getContent());
        assertEquals(result.getId(), post.getId());
    }

    @Test
    void testPostNotExistForDeletePost() {
        when(postRepository.findById(-4L)).thenThrow(IllegalStateException.class);

        assertThrows(IllegalStateException.class,
                () -> postService.deletePost(-4));
    }

    @Test
    void testSuccessfulDeletePost() {
        when(postRepository.findById(4L)).thenReturn(Optional.of(post));

        postService.deletePost(4);
        Mockito.verify(postRepository).save(postCaptor.capture());

        Post postResult = postCaptor.getValue();
        assertTrue(postResult.isDeleted());
        assertFalse(postResult.isPublished());
        assertNotNull(postResult.getUpdatedAt());
    }

    @Test
    void testNullAuthorPostsList() {
        filterDto.setAuthor(true);
        when(postRepository.findByAuthorIdWithLikes(4L)).thenReturn(null);
        List<PostDto> result = postService.getPostsById(4L, filterDto);


        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testNullPostPostsList() {
        filterDto.setAuthor(false);
        when(postRepository.findByProjectIdWithLikes(4L)).thenReturn(null);
        List<PostDto> result = postService.getPostsById(4L, filterDto);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSuccessfulGetAuthorUnpostedPosts() {
        filterDto.setAuthor(true);
        filterDto.setDeleted(false);
        filterDto.setPublished(false);
        filterDto.setPostField(PostField.CREATED_AT);

        List<Post> posts = getPosts(false);

        Post firstInappropriatePost = new Post();
        Post secondInappropriatePost = new Post();

        firstInappropriatePost.setDeleted(true);
        secondInappropriatePost.setPublished(true);

        posts.add(firstInappropriatePost);
        posts.add(secondInappropriatePost);

        when(postRepository.findByAuthorIdWithLikes(4L))
                .thenReturn(posts);

        List<PostDto> postsDto = postService.getPostsById(4L, filterDto);

        assertEquals(postsDto.size(), 3);
        assertEquals(postsDto.get(0).getId(), 3);
        assertEquals(postsDto.get(1).getId(), 1);
        assertEquals(postsDto.get(2).getId(), 2);
    }

    @Test
    void testSuccessfulGetAuthorPostedPosts() {
        filterDto.setAuthor(true);
        filterDto.setDeleted(false);
        filterDto.setPublished(true);
        filterDto.setPostField(PostField.PUBLISHED_AT);

        List<Post> posts = getPosts(true);

        Post firstInappropriatePost = new Post();
        Post secondInappropriatePost = new Post();

        firstInappropriatePost.setDeleted(true);
        secondInappropriatePost.setPublished(false);

        posts.add(firstInappropriatePost);
        posts.add(secondInappropriatePost);

        when(postRepository.findByAuthorIdWithLikes(4L))
                .thenReturn(posts);

        List<PostDto> postsDto = postService.getPostsById(4L, filterDto);

        assertEquals(postsDto.size(), 3);
        assertEquals(postsDto.get(0).getId(), 2);
        assertEquals(postsDto.get(1).getId(), 1);
        assertEquals(postsDto.get(2).getId(), 3);
    }

    private List<Post> getPosts(boolean published) {
        Post firstPost = new Post();
        Post secondPost = new Post();
        Post thirdPost = new Post();

        Post[] posts = new Post[]{firstPost, secondPost, thirdPost};
        IntStream.range(0, posts.length).forEach(i -> {
            posts[i].setPublished(published);
            posts[i].setDeleted(false);
            posts[i].setId((long) i + 1);
        });

        thirdPost.setCreatedAt(LocalDateTime.of(2024, 10, 10, 10, 10));
        firstPost.setCreatedAt(LocalDateTime.of(2024, 10, 11, 14, 10));
        secondPost.setCreatedAt(LocalDateTime.of(2024, 10, 14, 10, 10));

        thirdPost.setPublishedAt(LocalDateTime.of(2024, 10, 10, 10, 19));
        firstPost.setPublishedAt(LocalDateTime.of(2024, 10, 10, 10, 16));
        secondPost.setPublishedAt(LocalDateTime.of(2024, 10, 10, 10, 14));

        return new ArrayList<>(List.of(firstPost, secondPost, thirdPost));
    }
}