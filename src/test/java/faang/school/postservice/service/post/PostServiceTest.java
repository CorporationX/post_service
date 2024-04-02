package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.post.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.post.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private PostValidator postValidator;
    @Spy
    private PostMapperImpl postMapper;
    private ExecutorService postPublisherThreadPool;
    private PostService postService;

    private Post firstPost;
    private Post secondPost;
    private Post thirdPost;
    private PostDto firstPostDto;

    @BeforeEach
    void setUp() {
        firstPost = Post.builder()
                .id(1L)
                .content("Valid content")
                .authorId(1L)
                .build();
        secondPost = Post.builder()
                .id(2L)
                .content("Valid content")
                .authorId(1L)
                .build();
        thirdPost = Post.builder()
                .id(3L)
                .content("Valid content")
                .authorId(1L)
                .build();
        firstPostDto = PostDto.builder()
                .id(firstPost.getId())
                .content(firstPost.getContent())
                .authorId(firstPost.getAuthorId())
                .build();
        postPublisherThreadPool = Executors.newFixedThreadPool(10);
        postService = new PostService(postRepository, postValidator, postMapper, postPublisherThreadPool);
    }

    @Test
    void create_PostCreated_ThenReturnedAsDto() {
        when(postRepository.save(firstPost)).thenReturn(firstPost);

        PostDto returned = postService.create(firstPostDto);

        assertAll(
                () -> verify(postValidator, times(1)).validatePostAuthor(firstPostDto),
                () -> verify(postValidator, times(1)).validateIfAuthorExists(firstPostDto),
                () -> verify(postRepository, times(1)).save(firstPost),
                () -> verify(postMapper, times(1)).toEntity(firstPostDto),
                () -> verify(postMapper, times(1)).toDto(firstPost),
                () -> assertEquals(firstPostDto, returned)
        );
    }

    @Test
    void getPostById_PostFound_thenReturnedAsDto() {
        when(postRepository.findById(firstPost.getId())).thenReturn(Optional.ofNullable(firstPost));

        PostDto returned = postService.getPostById(firstPost.getId());

        assertAll(
                () -> verify(postRepository, times(1)).findById(firstPost.getId()),
                () -> verify(postMapper, times(1)).toDto(firstPost),
                () -> assertEquals(firstPostDto, returned)
        );
    }

    @Test
    void getPostById_PostNotFound_ShouldThrowEntityNotFoundException() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> postService.getPostById(1299L));
    }

    @Test
    void publish_PostPublished_ThenReturnedAsDto() {
        when(postRepository.findById(firstPost.getId())).thenReturn(Optional.ofNullable(firstPost));
        when(postRepository.save(firstPost)).thenReturn(firstPost);

        PostDto returned = postService.publish(firstPost.getId());

        assertAll(
                () -> verify(postRepository, times(1)).findById(firstPost.getId()),
                () -> verify(postRepository, times(1)).save(firstPost),
                () -> verify(postMapper, times(1)).toDto(firstPost),
                () -> assertTrue(returned.isPublished()),
                () -> assertNotNull(returned.getPublishedAt()),
                () -> assertNotEquals(firstPostDto, returned)
        );
    }

    @Test
    void publishScheduledPosts() throws NoSuchFieldException, IllegalAccessException {
        List<Post> posts = new ArrayList<>(List.of(firstPost, secondPost, thirdPost));
        Field batchSize = PostService.class.getDeclaredField("scheduledPostsBatchSize");
        batchSize.setAccessible(true);
        batchSize.set(postService, 1000);
        when(postRepository.findReadyToPublish()).thenReturn(posts);
        when(postRepository.saveAll(posts)).thenReturn(posts);

        List<PostDto> result = postService.publishScheduledPosts();

        assertAll(
                () -> verify(postRepository, times(1)).findReadyToPublish(),
                () -> verify(postRepository, times(1)).saveAll(posts),
                () -> verify(postMapper, times(1)).toDto(firstPost),
                () -> verify(postMapper, times(1)).toDto(secondPost),
                () -> verify(postMapper, times(1)).toDto(thirdPost),
                () -> assertEquals(List.of(true, true, true), result.stream().map(PostDto::isPublished).toList()),
                () -> assertNotNull(firstPost.getPublishedAt()),
                () -> assertNotNull(secondPost.getPublishedAt()),
                () -> assertNotNull(thirdPost.getPublishedAt())
        );
    }

    @Test
    void update_PostUpdated_ThenReturnedAsDto() {
        firstPost.setContent("Old content");
        firstPostDto.setContent("Updated content");
        when(postRepository.findById(firstPost.getId())).thenReturn(Optional.ofNullable(firstPost));
        when(postRepository.save(firstPost)).thenReturn(firstPost);

        PostDto returned = postService.update(firstPostDto);

        assertAll(
                () -> verify(postRepository, times(1)).findById(firstPost.getId()),
                () -> verify(postMapper, times(1)).toDto(firstPost),
                () -> verify(postRepository, times(1)).save(firstPost),
                () -> assertEquals(firstPostDto, returned),
                () -> assertNotEquals("Old content", firstPost.getContent())
        );
    }

    @Test
    void delete_PostWasMarkedAsDeleted_ThenSavedToDb() {
        when(postRepository.findById(firstPost.getId())).thenReturn(Optional.ofNullable(firstPost));
        when(postRepository.save(firstPost)).thenReturn(firstPost);

        postService.delete(firstPost.getId());

        assertAll(
                () -> verify(postRepository, times(1)).findById(firstPost.getId()),
                () -> verify(postRepository, times(1)).save(firstPost),
                () -> assertTrue(firstPost.isDeleted())
        );
    }

    @Test
    void getCreatedPostsByUserId_PostsFilteredAndSorted_ThenReturnedAsDto() {
        setPostsCreationDates();
        firstPost.setPublished(true);
        when(postRepository.findByAuthorId(anyLong())).thenReturn(List.of(firstPost, secondPost, thirdPost));

        List<PostDto> returned = postService.getCreatedPostsByUserId(1L);

        assertAll(
                () -> verify(postRepository, times(1)).findByAuthorId(1L),
                () -> verify(postMapper, times(1)).toDto(List.of(thirdPost, secondPost)),
                () -> assertEquals(2, returned.size()),
                () -> assertEquals(postMapper.toDto(thirdPost), returned.get(0))
        );
    }

    @Test
    void getCreatedPostsByProjectId_PostsFilteredAndSorted_ThenReturnedAsDto() {
        setProjectInsteadOfAuthor();
        setPostsCreationDates();
        firstPost.setPublished(true);
        when(postRepository.findByProjectId(anyLong())).thenReturn(List.of(firstPost, secondPost, thirdPost));

        List<PostDto> returned = postService.getCreatedPostsByProjectId(2L);

        assertAll(
                () -> verify(postRepository, times(1)).findByProjectId(2L),
                () -> verify(postMapper, times(1)).toDto(List.of(thirdPost, secondPost)),
                () -> assertEquals(2, returned.size()),
                () -> assertEquals(postMapper.toDto(secondPost), returned.get(1))
        );
    }

    @Test
    void getPublishedPostsByUserId_PostsFilteredAndSorted_ThenReturnedAsDto() {
        setPublishedForAllPosts();
        setPostsPublishDates();
        thirdPost.setDeleted(true);
        when(postRepository.findByAuthorId(anyLong())).thenReturn(List.of(firstPost, secondPost, thirdPost));

        List<PostDto> returned = postService.getPublishedPostsByUserId(1L);

        assertAll(
                () -> verify(postRepository, times(1)).findByAuthorId(1L),
                () -> verify(postMapper, times(1)).toDto(List.of(secondPost, firstPost)),
                () -> assertEquals(2, returned.size()),
                () -> assertEquals(postMapper.toDto(secondPost), returned.get(0))
        );
    }

    @Test
    void getPublishedPostsByProjectId_PostsFilteredAndSorted_ThenReturnedAsDto() {
        setPublishedForAllPosts();
        setPostsPublishDates();
        setProjectInsteadOfAuthor();
        thirdPost.setDeleted(true);
        when(postRepository.findByProjectId(anyLong())).thenReturn(List.of(firstPost, secondPost, thirdPost));

        List<PostDto> returned = postService.getPublishedPostsByProjectId(2L);

        assertAll(
                () -> verify(postRepository, times(1)).findByProjectId(2L),
                () -> verify(postMapper, times(1)).toDto(List.of(secondPost, firstPost)),
                () -> assertEquals(2, returned.size()),
                () -> assertEquals(postMapper.toDto(firstPost), returned.get(1))
        );
    }

    private void setPostsCreationDates() {
        firstPost.setCreatedAt(LocalDateTime.now().minusDays(10));
        secondPost.setCreatedAt(LocalDateTime.now().minusDays(7));
        thirdPost.setCreatedAt(LocalDateTime.now().minusDays(2));
    }

    private void setPostsPublishDates() {
        firstPost.setPublishedAt(LocalDateTime.now().minusDays(10));
        secondPost.setPublishedAt(LocalDateTime.now().minusDays(7));
        thirdPost.setPublishedAt(LocalDateTime.now().minusDays(2));
    }

    private void setProjectInsteadOfAuthor() {
        firstPost.setAuthorId(null);
        firstPost.setProjectId(2L);
        secondPost.setAuthorId(null);
        secondPost.setProjectId(2L);
        thirdPost.setAuthorId(null);
        thirdPost.setProjectId(2L);
    }

    private void setPublishedForAllPosts() {
        firstPost.setPublished(true);
        secondPost.setPublished(true);
        thirdPost.setPublished(true);
    }
}
