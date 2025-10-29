package faang.school.postservice.util.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.TextCheck;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.LanguageToolConfig;
import faang.school.postservice.dto.post.CreatePostRequestDto;
import faang.school.postservice.dto.post.UpdatePostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.text.MatchDto;
import faang.school.postservice.dto.text.ReplacementDto;
import faang.school.postservice.dto.text.TextResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostServiceImpl;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    private static final long POST_ID = 1L;
    private static final long AUTHOR_ID = 10L;
    private static final long PROJECT_ID = 7L;
    private static final long SAVED_ID = 42L;

    private static final String CONTENT = "Hello";
    private static final String CONTENT_CREATE = "Hi";
    private static final String CONTENT_NEW = "New";

    @Mock
    PostRepository postRepository;
    @Mock
    UserServiceClient userServiceClient;
    @Mock
    ProjectServiceClient projectServiceClient;
    @Mock
    TextCheck textCheck;
    @Mock
    LanguageToolConfig languageToolConfig;

    @Spy
    PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    @InjectMocks
    PostServiceImpl service;

    private Post postDbEntity;

    @BeforeEach
    void setUp() {
        postDbEntity = buildPost(POST_ID, AUTHOR_ID, null, CONTENT, false, false,
                LocalDateTime.now().minusHours(1), null);
    }

    @Test
    @DisplayName("update: does nothing if content unchanged")
    void update_same_content_no_changes() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(postDbEntity));
        when(postRepository.save(any(Post.class))).thenAnswer(inv -> inv.getArgument(0));

        // same content as existing entity
        UpdatePostRequestDto dto = new UpdatePostRequestDto(CONTENT);

        PostResponseDto out = service.update(POST_ID, dto);

        verify(postMapper, times(1)).toDto(any(Post.class));
        assertEquals(CONTENT, out.content());
        assertEquals(postDbEntity.getCreatedAt(), out.createdAt());
        assertFalse(out.published());
        assertNull(out.publishedAt());
        assertNotNull(out.updatedAt());
    }

    @Test
    @DisplayName("createDraft: ok (author user) + timestamps")
    void createDraft_user_ok() {
        CreatePostRequestDto input = new CreatePostRequestDto(CONTENT_CREATE, AUTHOR_ID, null);
        when(userServiceClient.getUser(AUTHOR_ID))
                .thenReturn(ResponseEntity.ok(new UserDto(AUTHOR_ID, "name","spb@ru")));

        when(postRepository.save(any(Post.class))).thenAnswer(inv -> {
            Post p = inv.getArgument(0);
            p.setId(SAVED_ID);
            var now = LocalDateTime.now();
            p.setCreatedAt(now);
            p.setUpdatedAt(now);
            return p;
        });

        PostResponseDto out = service.createDraft(input);

        verify(postMapper, times(1)).toDto(any(Post.class));
        assertEquals(SAVED_ID, out.id());
        assertEquals(AUTHOR_ID, out.authorId());
        assertFalse(out.published());
        assertNotNull(out.createdAt());
        assertNotNull(out.updatedAt());
        assertNull(out.publishedAt());
    }

    @Test
    @DisplayName("createDraft: ok (project author)")
    void createDraft_project_ok() {
        CreatePostRequestDto input = new CreatePostRequestDto(CONTENT_CREATE, null, PROJECT_ID);
        when(projectServiceClient.getProject(PROJECT_ID))
                .thenReturn(ResponseEntity.ok(new ProjectDto(PROJECT_ID, "name")));

        when(postRepository.save(any(Post.class))).thenAnswer(inv -> {
            Post p = inv.getArgument(0);
            p.setId(SAVED_ID);
            var now = LocalDateTime.now();
            p.setCreatedAt(now);
            p.setUpdatedAt(now);
            return p;
        });

        PostResponseDto out = service.createDraft(input);

        verify(postMapper, times(1)).toDto(any(Post.class));
        assertEquals(SAVED_ID, out.id());
        assertEquals(PROJECT_ID, out.projectId());
        assertFalse(out.published());
        assertNull(out.publishedAt());
    }

    @Test
    @DisplayName("createDraft: both authors -> error")
    void createDraft_bothAuthors_error() {
        CreatePostRequestDto input = new CreatePostRequestDto(CONTENT_CREATE, AUTHOR_ID, PROJECT_ID);
        assertThrows(IllegalArgumentException.class, () -> service.createDraft(input));
        verify(postMapper, never()).toDto(any());
        verify(postRepository, never()).save(any());
    }

    @Test
    @DisplayName("createDraft: user not found in external service -> error")
    void createDraft_user_not_found_error() {
        CreatePostRequestDto input = new CreatePostRequestDto(CONTENT_CREATE, AUTHOR_ID, null);
        when(userServiceClient.getUser(AUTHOR_ID)).thenThrow(mock(FeignException.NotFound.class));

        assertThrows(IllegalArgumentException.class, () -> service.createDraft(input));

        verify(userServiceClient, times(1)).getUser(AUTHOR_ID);
        verify(projectServiceClient, never()).getProject(anyLong());
        verify(postRepository, never()).save(any(Post.class));
        verify(postMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("createDraft: project not found in external service -> error")
    void createDraft_project_not_found_error() {
        CreatePostRequestDto input = new CreatePostRequestDto(CONTENT_CREATE, null, PROJECT_ID);
        when(projectServiceClient.getProject(PROJECT_ID)).thenThrow(mock(FeignException.NotFound.class));

        assertThrows(IllegalArgumentException.class, () -> service.createDraft(input));

        verify(projectServiceClient, times(1)).getProject(PROJECT_ID);
        verify(userServiceClient, never()).getUser(anyLong());
        verify(postRepository, never()).save(any(Post.class));
        verify(postMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("publish: ok")
    void publish_ok() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(postDbEntity));
        when(postRepository.save(any(Post.class))).thenAnswer(inv -> inv.getArgument(0));

        PostResponseDto out = service.publish(POST_ID);

        verify(postMapper, times(1)).toDto(any(Post.class));
        assertTrue(out.published());
        assertNotNull(out.publishedAt());
    }

    @Test
    @DisplayName("publish: already published -> error")
    void publish_already_error() {
        postDbEntity.setPublished(true);
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(postDbEntity));
        assertThrows(IllegalStateException.class, () -> service.publish(POST_ID));
        verify(postMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("publish: deleted post -> error")
    void publish_deleted_error() {
        postDbEntity.setDeleted(true);
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(postDbEntity));

        assertThrows(IllegalStateException.class, () -> service.publish(POST_ID));
        verify(postMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("update: change content only + updates timestamp")
    void update_change_content() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(postDbEntity));
        when(postRepository.save(any(Post.class))).thenAnswer(inv -> {
            Post p = inv.getArgument(0);
            p.setUpdatedAt(LocalDateTime.now());
            return p;
        });

        PostResponseDto out = service.update(POST_ID, new UpdatePostRequestDto(CONTENT_NEW));

        verify(postMapper, times(1)).toDto(any(Post.class));
        assertEquals(CONTENT_NEW, out.content());
        assertNotNull(out.updatedAt());
        assertFalse(out.published());
        assertNull(out.publishedAt());
    }

    @Test
    @DisplayName("softDelete: idempotent + unpublish: second call should not throw or change flags")
    void softDelete_ok() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(postDbEntity));
        when(postRepository.save(any(Post.class))).thenAnswer(inv -> inv.getArgument(0));

        service.softDelete(POST_ID);
        assertTrue(postDbEntity.isDeleted());
        assertFalse(postDbEntity.isPublished());

        service.softDelete(POST_ID);
        assertTrue(postDbEntity.isDeleted());
        assertFalse(postDbEntity.isPublished());
        assertNotNull(postDbEntity.getUpdatedAt());
        verify(postMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("getById: ok")
    void getById_ok() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(postDbEntity));
        PostResponseDto out = service.getById(POST_ID);
        verify(postMapper, times(1)).toDto(postDbEntity);
        assertEquals(POST_ID, out.id());
    }

    @Test
    @DisplayName("getById: not found -> error")
    void getById_not_found() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.getById(POST_ID));
        verify(postMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("lists: filters and sorts by user (findByAuthorId)")
    void lists_by_user_filters_and_sorts() {
        Post p1 = buildPost(1L, AUTHOR_ID, "a", false, false,
                LocalDateTime.now(), null);
        Post p2 = buildPost(2L, AUTHOR_ID, "b", true, false,
                LocalDateTime.now().minusMinutes(5), LocalDateTime.now());
        Post p3 = buildPost(3L, AUTHOR_ID, "c", false, true,
                LocalDateTime.now().minusHours(2), null);

        when(postRepository.findByAuthorId(AUTHOR_ID)).thenReturn(List.of(p1, p2, p3));

        var drafts = service.getDraftsByUser(AUTHOR_ID);
        var published = service.getPublishedByUser(AUTHOR_ID);

        verify(postMapper, atLeastOnce()).toDto(any(Post.class));
        assertEquals(1, drafts.size());
        assertEquals(1, published.size());
        assertFalse(drafts.get(0).published());
        assertTrue(published.get(0).published());
    }

    @Test
    @DisplayName("lists: filters and sorts by project (findByProjectId)")
    void lists_by_project_filters_and_sorts() {
        Post p1 = buildPost(11L, null, PROJECT_ID, "pa", false, false,
                LocalDateTime.now(), null);
        Post p2 = buildPost(12L, null, PROJECT_ID, "pb", true, false,
                LocalDateTime.now().minusMinutes(3), LocalDateTime.now());
        Post p3 = buildPost(13L, null, PROJECT_ID, "pc", false, true,
                LocalDateTime.now().minusHours(2), null);

        when(postRepository.findByProjectId(PROJECT_ID)).thenReturn(List.of(p1, p2, p3));

        var drafts = service.getDraftsByProject(PROJECT_ID);
        var published = service.getPublishedByProject(PROJECT_ID);

        verify(postMapper, atLeastOnce()).toDto(any(Post.class));
        assertEquals(1, drafts.size());
        assertEquals(1, published.size());
        assertFalse(drafts.get(0).published());
        assertTrue(published.get(0).published());
    }

    @Test
    @DisplayName("createDraft: sets createdAt & updatedAt (JPA timestamps), published=false, publishedAt=null")
    void createDraft_setsTimestamps_andDraftFlags() {
        CreatePostRequestDto input = new CreatePostRequestDto(
                CONTENT_CREATE,
                AUTHOR_ID,
                null
        );

        when(userServiceClient.getUser(AUTHOR_ID))
                .thenReturn(ResponseEntity.ok(new UserDto(AUTHOR_ID, "name","spb@ru")));

        when(postRepository.save(any(Post.class))).thenAnswer(inv -> {
            Post p = inv.getArgument(0);
            p.setId(SAVED_ID);
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            p.setCreatedAt(now);
            p.setUpdatedAt(now);
            return p;
        });

        PostResponseDto out = service.createDraft(input);

        verify(postMapper, times(1)).toDto(any(Post.class));
        assertEquals(SAVED_ID, out.id());
        assertFalse(out.published());
        assertNull(out.publishedAt());
        assertNotNull(out.createdAt());
        assertNotNull(out.updatedAt());
        assertEquals(out.createdAt(), out.updatedAt());
    }

    @Test
    @DisplayName("publish: sets publishedAt only on publish; keeps createdAt; toggles published=true")
    void publish_sets_publishedAt_only() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(postDbEntity));
        when(postRepository.save(any(Post.class))).thenAnswer(inv -> inv.getArgument(0));

        java.time.LocalDateTime createdBefore = postDbEntity.getCreatedAt();

        PostResponseDto out = service.publish(POST_ID);

        verify(postMapper, times(1)).toDto(any(Post.class));
        assertTrue(out.published());
        assertNotNull(out.publishedAt());
        assertEquals(createdBefore, out.createdAt());
        assertNotNull(out.updatedAt());
    }

    @Test
    @DisplayName("update: changes content; does not change createdAt; updates updatedAt")
    void update_bumps_updatedAt_only() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(postDbEntity));
        when(postRepository.save(any(Post.class))).thenAnswer(inv -> {
            Post p = inv.getArgument(0);
            p.setUpdatedAt(java.time.LocalDateTime.now());
            return p;
        });

        java.time.LocalDateTime createdBefore = postDbEntity.getCreatedAt();

        PostResponseDto out = service.update(POST_ID, new UpdatePostRequestDto(CONTENT_NEW));

        verify(postMapper, times(1)).toDto(any(Post.class));
        assertEquals(CONTENT_NEW, out.content());
        assertEquals(createdBefore, out.createdAt());
        assertNotNull(out.updatedAt());
        assertTrue(out.updatedAt().isAfter(createdBefore));
        assertFalse(out.published());
        assertNull(out.publishedAt());
    }

    @Test
    @DisplayName("softDelete: sets deleted=true and forces published=false; timestamps still valid")
    void softDelete_unpublishes() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(postDbEntity));
        when(postRepository.save(any(Post.class))).thenAnswer(inv -> inv.getArgument(0));

        service.softDelete(POST_ID);
        assertTrue(postDbEntity.isDeleted());
        assertFalse(postDbEntity.isPublished());
        assertNotNull(postDbEntity.getUpdatedAt());
    }

    private Post buildPost(Long id, Long authorId, String content,
                           boolean published, boolean deleted,
                           LocalDateTime createdAt, LocalDateTime publishedAt) {
        return buildPost(id, authorId, null, content, published, deleted, createdAt, publishedAt);
    }

    private Post buildPost(Long id, Long authorId, Long projectId, String content,
                           boolean published, boolean deleted,
                           LocalDateTime createdAt, LocalDateTime publishedAt) {
        return Post.builder()
                .id(id)
                .authorId(authorId)
                .projectId(projectId)
                .content(content)
                .published(published)
                .deleted(deleted)
                .createdAt(createdAt)
                .publishedAt(publishedAt)
                .build();
    }

    @Test
    @DisplayName("processTextChecking: corrects unpublished posts")
    void processTextCheckingCorrectsUnpublishedPosts() {
        String textWithError = "Hello worlld";
        String expectedCorrected = "Hello world";

        Post unpublishedPost = buildPost(1L, AUTHOR_ID, textWithError, false, false,
                LocalDateTime.now(), null);

        when(postRepository.findReadyToPublish()).thenReturn(List.of(unpublishedPost));

        TextResponseDto correctionResponse = new TextResponseDto(List.of(
                new MatchDto(6, 6, List.of(new ReplacementDto("world")))
        ));

        when(languageToolConfig.getLanguage()).thenReturn("auto");
        when(textCheck.checkText(textWithError, "auto")).thenReturn(correctionResponse);

        when(postRepository.findById(1L)).thenReturn(Optional.of(unpublishedPost));
        when(postRepository.save(any(Post.class))).thenReturn(unpublishedPost);

        service.processTextChecking();

        verify(textCheck).checkText(textWithError, "auto");
        verify(postRepository).save(argThat(savedPost ->
                savedPost.getContent().equals(expectedCorrected)
        ));
    }

    @Test
    @DisplayName("checkTextWithRetry: retries on failure with backoff")
    void checkTextWithRetryRetriesOnFailure() {
        String text = "Test text";
        TextResponseDto successResponse = new TextResponseDto(List.of());

        when(languageToolConfig.getLanguage()).thenReturn("auto");
        when(textCheck.checkText(text, "auto"))
                .thenThrow(new RuntimeException("API error"))
                .thenThrow(new RuntimeException("API error"))
                .thenReturn(successResponse);

        TextResponseDto result = service.checkTextWithRetry(text);

        verify(textCheck, times(3)).checkText(text, "auto");
        assertNotNull(result);
        assertEquals(successResponse, result);
    }

    @Test
    @DisplayName("processTextChecking: no changes when no corrections needed")
    void processTextCheckingNoChangesWhenNoCorrections() {
        String correctText = "Hello world";
        Post unpublishedPost = buildPost(1L, AUTHOR_ID, correctText, false, false,
                LocalDateTime.now(), null);

        when(postRepository.findReadyToPublish()).thenReturn(List.of(unpublishedPost));

        TextResponseDto emptyResponse = new TextResponseDto(List.of());

        when(languageToolConfig.getLanguage()).thenReturn("auto");
        when(textCheck.checkText(correctText, "auto")).thenReturn(emptyResponse);

        service.processTextChecking();

        verify(textCheck).checkText(correctText, "auto");
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("processTextChecking: handles null response from LanguageTool")
    void processTextCheckingHandlesNullResponse() {
        Post unpublishedPost = buildPost(1L, AUTHOR_ID, "Some text", false, false,
                LocalDateTime.now(), null);

        when(postRepository.findReadyToPublish()).thenReturn(List.of(unpublishedPost));

        when(languageToolConfig.getLanguage()).thenReturn("auto");
        when(textCheck.checkText("Some text", "auto")).thenReturn(null);

        service.processTextChecking();

        verify(textCheck).checkText("Some text", "auto");
        verify(postRepository, never()).save(any(Post.class));
    }
}