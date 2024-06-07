package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.moderation.logic.PostModerator;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.PostValidator;
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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @Mock
    private PostValidator postValidator;

    @Spy
    private PostMapperImpl postMapper;

    @Mock
    private PostModerator postModerator;

    private PostDto postDto;
    private long postId;
    private Post postToUpdate;
    private List<Post> posts;
    private Long id;
    private LocalDateTime time;

    @BeforeEach
    void setUp() {
        id = 1L;
        postDto = PostDto.builder()
                .content("qwe")
                .projectId(3L)
                .authorId(3L)
                .build();

        postId = 10L;
        postToUpdate = Post.builder()
                .id(postId)
                .content("123123123")
                .authorId(3L)
                .published(false)
                .build();

        time = LocalDateTime.now();

        posts = new ArrayList<>();
        posts.add(Post.builder().content("1").deleted(false).published(true).createdAt(time.minusDays(12)).build());
        posts.add(Post.builder().content("2").deleted(false).published(true).createdAt(time.minusDays(1)).build());
        posts.add(Post.builder().content("3").deleted(true).published(true).createdAt(time.minusDays(2)).build());

        posts.add(Post.builder().content("4").deleted(false).published(false).createdAt(time.minusDays(12)).build());
        posts.add(Post.builder().content("5").deleted(false).published(false).createdAt(time.minusDays(1)).build());
        posts.add(Post.builder().content("6").deleted(true).published(false).createdAt(time.minusDays(2)).build());
    }

    @Test
    void createDraftPostUserNotExist() {
        when(userServiceClient.getUser(postDto.getAuthorId())).thenThrow(DataValidationException.class);
        assertThrows(DataValidationException.class, () -> postService.createDraftPost(postDto));
    }

    @Test
    void createDraftPostProjectNotExist() {
        when(projectServiceClient.getProject(postDto.getProjectId())).thenThrow(DataValidationException.class);
        assertThrows(DataValidationException.class, () -> postService.createDraftPost(postDto));
    }

    @Test
    void createDraftPost() {
        Post post = postMapper.toEntity(postDto);

        postService.createDraftPost(postDto);
        verify(postRepository, times(1)).save(post);
    }

    @Test
    void publishDraftPostNotExist() {
        when(postRepository.findById(postId)).thenThrow(DataValidationException.class);

        assertThrows(DataValidationException.class, () -> postService.publishDraftPost(postId));
    }

    @Test
    void publishDraftPost() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(postToUpdate));
        PostDto actual = postService.publishDraftPost(postId);

        assertTrue(actual.isPublished());
    }

    @Test
    void updatePost() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(postToUpdate));
        PostDto actual = postService.updatePost(postDto, postId);

        assertEquals("qwe", actual.getContent());
    }

    @Test
    void deletePost() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(postToUpdate));
        postService.deletePost(postId);

        assertTrue(postToUpdate.isDeleted());
    }

    @Test
    void getPost() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(postToUpdate));
        PostDto expected = postMapper.toDto(postToUpdate);

        assertEquals(expected, postService.getPost(postId));
    }

    @Test
    void getUserDrafts() {
        when(postRepository.findByAuthorId(id)).thenReturn(posts);

        List<PostDto> actual = postService.getUserDrafts(id);

        assertAll(
                () -> assertEquals(2, actual.size()),
                () -> assertEquals("5", actual.get(0).getContent()),
                () -> assertEquals("4", actual.get(1).getContent())
        );
    }

    @Test
    void getProjectDrafts() {
        when(postRepository.findByProjectId(id)).thenReturn(posts);

        List<PostDto> actual = postService.getProjectDrafts(id);

        assertAll(
                () -> assertEquals(2, actual.size()),
                () -> assertEquals("5", actual.get(0).getContent()),
                () -> assertEquals("4", actual.get(1).getContent())
        );
    }

    @Test
    void getUserPosts() {
        when(postRepository.findByAuthorId(id)).thenReturn(posts);

        List<PostDto> actual = postService.getUserPosts(id);

        assertAll(
                () -> assertEquals(2, actual.size()),
                () -> assertEquals("2", actual.get(0).getContent()),
                () -> assertEquals("1", actual.get(1).getContent())
        );
    }

    @Test
    void getProjectPosts() {
        when(postRepository.findByProjectId(id)).thenReturn(posts);

        List<PostDto> actual = postService.getProjectPosts(id);

        assertAll(
                () -> assertEquals(2, actual.size()),
                () -> assertEquals("2", actual.get(0).getContent()),
                () -> assertEquals("1", actual.get(1).getContent())
        );
    }

    @Test
    void moderatePosts() {
        when(postRepository.findAllUnverifiedPosts()).thenReturn(posts);
        postService.moderatePosts();

        verify(postModerator, times(1)).moderatePosts(posts);
    }

}
