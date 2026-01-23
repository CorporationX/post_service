package faang.school.postservice.util.post;

import faang.school.postservice.client.FeignLanguageToolClient;
import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.LanguageToolConfig;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.CreatePostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.scheduler.ThreadPoolConfig;
import faang.school.postservice.service.FollowersService;
import faang.school.postservice.service.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    private static final long POST_ID = 1L;
    private static final long AUTHOR_ID = 10L;
    private static final long PROJECT_ID = 7L;
    private static final long SAVED_ID = 42L;

    private static final String CONTENT = "Hello";
    private static final String CONTENT_CREATE = "Hi";
    private static final String CONTENT_NEW = "New";

    private static final int POOL_SIZE = 2;
    private static final int BATCH_SIZE = 2;

    // deterministic "now" for stable tests
    private static final LocalDateTime NOW = LocalDateTime.of(2025, 1, 1, 12, 0);

    @Mock PostRepository postRepository;
    @Mock UserServiceClient userServiceClient;
    @Mock ProjectServiceClient projectServiceClient;
    @Mock LanguageToolConfig languageToolConfig;
    @Mock ThreadPoolConfig threadPoolConfig;
    @Mock FeignLanguageToolClient feignLanguageTool;
    @Mock UserContext userContext;
    @Mock FollowersService followersService;
    @Mock ApplicationEventPublisher applicationEventPublisher;

    @Spy
    private PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    @Spy
    @InjectMocks
    PostServiceImpl service;

    private Post postDbEntity;

    @BeforeEach
    void setUp() {
        postDbEntity = buildPost(
                POST_ID,
                AUTHOR_ID,
                null,
                CONTENT,
                false,
                false,
                NOW.minusHours(1),
                null
        );
    }

    @Test
    @DisplayName("createDraft: ok (author user) + timestamps")
    void createDraft_user_ok() {
        // given
        CreatePostRequestDto input = new CreatePostRequestDto(CONTENT_CREATE, AUTHOR_ID, null);

        when(userServiceClient.getUser(AUTHOR_ID))
                .thenReturn(okUser(user(AUTHOR_ID, "name", "spb@ru")));

        when(postRepository.save(any(Post.class)))
                .thenAnswer(saveAssigningIdAndTimestamps(SAVED_ID));

        // when
        PostResponseDto out = service.createDraft(input);

        // then
        verify(postMapper).toDto(any(Post.class));
        assertEquals(SAVED_ID, out.id());
        assertEquals(AUTHOR_ID, out.authorId());
        assertFalse(out.published());
        assertNotNull(out.createdAt());
        assertNotNull(out.updatedAt());
        assertNull(out.publishedAt());
    }

    @Test
    @DisplayName("createDraft: sets createdAt & updatedAt (JPA timestamps), published=false, publishedAt=null")
    void createDraft_setsTimestamps_andDraftFlags() {
        // given
        CreatePostRequestDto input = new CreatePostRequestDto(CONTENT_CREATE, AUTHOR_ID, null);

        when(userServiceClient.getUser(AUTHOR_ID))
                .thenReturn(okUser(user(AUTHOR_ID, "name", "spb@ru")));

        when(postRepository.save(any(Post.class)))
                .thenAnswer(saveAssigningIdAndTimestamps(SAVED_ID));

        // when
        PostResponseDto out = service.createDraft(input);

        // then
        verify(postMapper).toDto(any(Post.class));
        assertEquals(SAVED_ID, out.id());
        assertFalse(out.published());
        assertNull(out.publishedAt());
        assertNotNull(out.createdAt());
        assertNotNull(out.updatedAt());
        assertEquals(out.createdAt(), out.updatedAt());
    }

    // ---------------- helpers ----------------

    private static ResponseEntity<UserDto> okUser(UserDto dto) {
        return ResponseEntity.ok(dto);
    }

    private static UserDto user(long id, String username, String email) {
        return UserDto.builder()
                .id(id)
                .username(username)
                .email(email)
                .build();
    }

    private static org.mockito.stubbing.Answer<Post> saveAssigningIdAndTimestamps(long id) {
        return inv -> {
            Post p = inv.getArgument(0);
            p.setId(id);
            // keep stable timestamps in tests
            p.setCreatedAt(NOW);
            p.setUpdatedAt(NOW);
            return p;
        };
    }

    private Post buildPost(Long id, Long authorId, Long projectId, String content,
                           boolean published, boolean deleted,
                           LocalDateTime createdAt, LocalDateTime publishedAt) {
        return Post.builder()
                .id(id)
                .published(published)
                .authorId(authorId)
                .projectId(projectId)
                .content(content)
                .deleted(deleted)
                .createdAt(createdAt)
                .publishedAt(publishedAt)
                .build();
    }
}
