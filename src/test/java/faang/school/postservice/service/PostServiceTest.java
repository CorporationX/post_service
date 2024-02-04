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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private Post post1;
    private Post post2;
    private Post post3;

    @BeforeEach
    public void init() {
        postDto.setContent("content");
        postDto.setAuthorId(1L);
        postDto.setProjectId(2L);

        LocalDateTime createdAt1 = LocalDateTime.of(2024, Month.JANUARY, 28, 1, 1, 1);
        LocalDateTime createdAt2 = LocalDateTime.of(2024, Month.JANUARY, 28, 1, 1, 2);
        LocalDateTime createdAt3 = LocalDateTime.of(2024, Month.JANUARY, 28, 1, 1, 3);


        post1 = Post.builder()
                .id(1L)
                .content("another content")
                .authorId(1L)
                .published(true)
                .deleted(false)
                .createdAt(createdAt1)
                .build();
        post2 = Post.builder()
                .id(2L)
                .authorId(1L)
                .content("2")
                .createdAt(createdAt2)
                .deleted(false)
                .published(false)
                .build();
        post3 = Post.builder()
                .id(3L)
                .authorId(1L)
                .content("3")
                .createdAt(createdAt3)
                .deleted(true)
                .published(true)
                .build();
    }

    @Test
    public void testCreateDraftSuccess() {
        postService.createPostDraft(postDto);
        Post post = postMapper.toEntity(postDto);
        Post savedPost = postRepository.save(post);
        assertSame(post, savedPost);
    }

    @Test
    public void testPublishPostSuccess() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post1));
        postService.publishPost(1L);
        assertTrue(post1.isPublished());
    }

    @Test
    public void testUpdatePostSuccess() {
        PostDto updatedDto = new PostDto();
        updatedDto.setContent("updated content");
        when(postRepository.findById(1L)).thenReturn(Optional.of(post1));
        postService.updatePost(1L, updatedDto);
        assertSame("updated content", post1.getContent());
    }

    @Test
    public void testDeletePostSuccess() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post1));
        postService.deletePost(1L);
        assertTrue(post1.isDeleted());
    }

    @Test
    public void testGetPostByIdSuccess() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post1));
        postService.getPost(1L);
        assertSame(post1, postService.getPost(1L));
    }

    @Test
    public void testGetAuthorDraftsSuccess() {
        List<Post> posts = List.of(post1, post2, post3);
        when(postRepository.findByAuthorId(1L)).thenReturn(posts);
        List<PostDto> postDtos = postService.getAuthorDrafts(1L);
        verify(postRepository, Mockito.times(1)).findByAuthorId(1L);
        assertEquals(1, postDtos.size());
    }

    @Test
    public void testGetProjectDraftsSuccess() {
        post1.setAuthorId(null);
        post1.setProjectId(1L);
        post2.setAuthorId(null);
        post2.setProjectId(1L);
        post3.setAuthorId(null);
        post3.setProjectId(1L);
        List<Post> posts = List.of(post1, post2, post3);
        lenient().when(postRepository.findByProjectId(1L)).thenReturn(posts);
        List<PostDto> postDtos = postService.getProjectDrafts(1L);
        assertEquals(0, postDtos.size());
    }

    @Test
    public void testGetAuthorPostsSuccess() {
        List<Post> posts = List.of(post1, post2, post3);
        when(postRepository.findByAuthorId(1L)).thenReturn(posts);
        List<PostDto> postDtos = postService.getAuthorDrafts(1L);
        assertEquals(1, postDtos.size());
    }

    @Test
    public void testGetProjectPostsSuccess() {
        post1.setAuthorId(null);
        post1.setProjectId(1L);
        post2.setAuthorId(null);
        post2.setProjectId(1L);
        post3.setAuthorId(null);
        post3.setProjectId(1L);
        List<Post> posts = List.of(post1, post2, post3);
        when(postRepository.findByProjectId(1L)).thenReturn(posts);
        List<PostDto> postDtos = postService.getProjectPosts(1L);
        assertEquals(1, postDtos.size());
    }

    @Test
    public void testSortDrafts() {
        long ownerId = 1;
        List<Post> posts = List.of(post1, post2, post3);
        lenient().when(postRepository.findByProjectId(ownerId)).thenReturn(posts);
        List<PostDto> expectedSortedDrafts = postMapper.toDtoList(List.of(post3, post2, post1));
        List<PostDto> sortedDrafts = postService.sortDrafts(ownerId);

        assertEquals(expectedSortedDrafts, sortedDrafts);
    }

    @Test
    public void testSortPosts() {
        long ownerId = 1;
        List<Post> posts = List.of(post1, post2, post3);
        lenient().when(postRepository.findByProjectId(ownerId)).thenReturn(posts);
        List<PostDto> expectedSortedDrafts = postMapper.toDtoList(List.of(post3, post2, post1));
        List<PostDto> sortedDrafts = postService.sortDrafts(ownerId);

        assertEquals(expectedSortedDrafts, sortedDrafts);
    }


}
