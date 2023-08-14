package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.post.ResponsePostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.ModerationDictionary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private PostRepository postRepository;
    @Spy
    private ResponsePostMapper responsePostMapper = ResponsePostMapper.INSTANCE;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectServiceClient projectServiceClient;
    @Mock
    private ModerationDictionary moderationDictionary;
    private final Integer batchSize = 100;
    private PostService postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        postService = new PostService(postRepository, responsePostMapper,
                userServiceClient, projectServiceClient, moderationDictionary, batchSize);
    }

    @Test
    void publishTest(){
        Post post = Post.builder().id(1L).published(false).deleted(false).build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        ResponsePostDto result = postService.publish(1L);

        assertTrue(result.isPublished());
    }

    @Test
    void updateTest() {
        Post post = Post.builder().id(1L).content("Before").build();
        UpdatePostDto dto = new UpdatePostDto(1L, "After");

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        ResponsePostDto result = postService.update(dto);

        assertEquals("After", post.getContent());
    }

    @Test
    void softDeleteTest() {
        Post post = Post.builder().id(1L).deleted(false).build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        ResponsePostDto result = postService.softDelete(1L);

        assertTrue(result.isDeleted());
    }

    @Test
    void createTest() {
        CreatePostDto correct = CreatePostDto.builder().authorId(1L).content("Content").build();
        UserDto userDto = new UserDto(1L, "username", "email@com");
        LocalDateTime now = LocalDateTime.now();
        Post post = Post.builder().authorId(1L).content("Content").createdAt(now).published(false).deleted(false).build();

        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(postRepository.save(any(Post.class))).thenReturn(post);

        ResponsePostDto result = postService.createDraft(correct);

        assertEquals(1, result.getAuthorId());
        assertEquals("Content", result.getContent());
        assertEquals(now, result.getCreatedAt());
        assertFalse(result.isPublished());
        assertFalse(result.isDeleted());
    }

    @Test
    void createThrowsExceptions() {
        CreatePostDto bothNotNull = CreatePostDto.builder().authorId(1L).projectId(1L).build();
        CreatePostDto blankContent = CreatePostDto.builder().authorId(1L).content("").build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> postService.createDraft(bothNotNull));
        assertThrows(NullPointerException.class, () -> postService.createDraft(blankContent));
        assertEquals("Both AuthorId and ProjectId can't be not null", exception.getMessage());
    }

    @Test
    void verifyContent_Test() {
        List<Post> posts = new ArrayList<>();

        for (int i = 0; i < batchSize * 2; i++) {
            Post post = Post.builder()
                    .id(i)
                    .verified(false)
                    .verifiedAt(null)
                    .build();
            if (post.getId() % 2 == 0) {
                post.setContent("Something arse something");
            } else {
                post.setContent("To verify");
            }
            posts.add(post);
        }

        when(postRepository.findAllByVerifiedAtIsNull()).thenReturn(posts);
        when(moderationDictionary.containsBadWord(anyString())).thenReturn(false);

        postService.verifyContent();

        verify(postRepository).findAllByVerifiedAtIsNull();
        verify(moderationDictionary, times(posts.size())).containsBadWord(anyString());
        verify(postRepository, times(posts.size() / batchSize)).saveAll(anyList());
    }
}