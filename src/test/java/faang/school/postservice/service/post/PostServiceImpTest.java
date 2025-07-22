package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.post.RepeatPublishException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostValidator postValidator;

    @Spy
    private PostMapper postMapper = new PostMapperImpl();

    @InjectMocks
    private PostServiceImpl postService;

    private final long testPostId = 1L;
    private final long testAuthorId = 11L;
    private final long testProjectId = 111L;
    private final String testOriginalContent = "content";
    private final String testUpdatedContent = "updated content";
    private final CreatePostDto createPostDto = new CreatePostDto(testOriginalContent, testAuthorId, null);
    private final UpdatePostDto updatePostDto = new UpdatePostDto(testPostId, testUpdatedContent);
    private final Post post = new Post();

    @Captor
    private ArgumentCaptor<Post> postCaptor;

    @BeforeEach
    void beforeEach() {
        post.setId(testPostId);
        post.setAuthorId(testAuthorId);
        post.setContent(testOriginalContent);
    }

    @Test
    void createDoesNotSaveIfValidationError() {
        doThrow(new EntityNotFoundException("Invalid user"))
                .when(postValidator)
                .validateCreate(createPostDto);

        assertThrows(EntityNotFoundException.class, () -> postService.create(createPostDto));
        verifyNoInteractions(postMapper);
        verifyNoInteractions(postRepository);
    }

    @Test
    void createValidatesSavesAndReturnsDto() {
        Post expectedPost = postMapper.toPost(createPostDto);
        when(postRepository.save(Mockito.any())).thenReturn(expectedPost);

        PostDto result = postService.create(createPostDto);

        verify(postValidator).validateCreate(createPostDto);
        verify(postRepository).save(postCaptor.capture());
        Post capturedPost = postCaptor.getValue();
        assertEquals(expectedPost.getContent(), result.content());
        assertEquals(expectedPost.getContent(), capturedPost.getContent());
    }

    @Test
    void publishSetsPublishedFieldsAndReturnsDto() {
        when(postRepository.findByIdAndDeletedFalse(testPostId)).thenReturn(Optional.of(post));

        PostDto result = postService.publish(testPostId);

        verify(postValidator).validatePublish(post);
        verify(postRepository).save(post);
        assertTrue(post.isPublished());
        assertInstanceOf(LocalDateTime.class, post.getPublishedAt());
        assertEquals(post.getContent(), result.content());
    }

    @Test
    void publishThrowsIfPostNotFound() {
        when(postRepository.findByIdAndDeletedFalse(testPostId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.publish(testPostId));
    }

    @Test
    void publishThrowsIfValidationError() {
        when(postRepository.findByIdAndDeletedFalse(testPostId)).thenReturn(Optional.of(post));
        doThrow(new RepeatPublishException("Already published"))
                .when(postValidator)
                .validatePublish(post);

        assertThrows(RepeatPublishException.class, () -> postService.publish(testPostId));
        verify(postRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateThrowsIfPostNotFound() {
        when(postRepository.findByIdAndDeletedFalse(updatePostDto.id())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.update(updatePostDto));
    }

    @Test
    void updateThrowsIfValidationError() {
        when(postRepository.findByIdAndDeletedFalse(updatePostDto.id())).thenReturn(Optional.of(post));
        doThrow(new EntityNotFoundException("Deleted post"))
                .when(postValidator)
                .validateUpdate(post);

        assertThrows(EntityNotFoundException.class, () -> postService.update(updatePostDto));
        verify(postRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateMapsUpdateAndReturnsDto() {
        when(postRepository.findByIdAndDeletedFalse(Mockito.anyLong())).thenReturn(Optional.of(post));

        PostDto result = postService.update(updatePostDto);

        verify(postValidator).validateUpdate(post);
        verify(postMapper).update(updatePostDto, post);
        verify(postRepository).save(postCaptor.capture());
        Post capturedPost = postCaptor.getValue();

        assertEquals(updatePostDto.content(), capturedPost.getContent());
        assertEquals(updatePostDto.content(), result.content());
    }

    @Test
    void deleteThrowsIfPostNotFound() {
        when(postRepository.findByIdAndDeletedFalse(testPostId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.delete(testPostId));
    }

    @Test
    void deleteSetsDeletedTrueAndSaves() {
        when(postRepository.findByIdAndDeletedFalse(testPostId)).thenReturn(Optional.of(post));

        postService.delete(testPostId);

        verify(postRepository).save(post);
        assertTrue(post.isDeleted());
    }

    @Test
    void getByIdThrowsIfPostNotFound() {
        when(postRepository.findByIdAndDeletedFalse(testPostId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.getById(testPostId));
    }

    @Test
    void getByIdReturnsMappedPostDto() {
        when(postRepository.findByIdAndDeletedFalse(testPostId)).thenReturn(Optional.of(post));

        PostDto result = postService.getById(testPostId);

        assertEquals(testPostId, result.id());
        assertEquals(testOriginalContent, result.content());
    }

    @Test
    void getDraftsByUserMapsAllToDtos() {
        when(postRepository.findByAuthorIdAndDeletedFalseOrderByCreatedAtDesc(testAuthorId)).thenReturn(List.of(post));

        List<PostDto> result = postService.getDraftsByUser(testAuthorId);

        assertSingleElementListWithTestPost(result);
    }


    private void assertSingleElementListWithTestPost(List<PostDto> result) {
        assertEquals(1, result.size());
        assertEquals(testPostId, result.get(0).id());
    }


    @Test
    void getDraftsByProjectMapsAllToDtos() {
        when(postRepository.findByProjectIdAndDeletedFalseOrderByCreatedAtDesc(testProjectId))
                .thenReturn(List.of(post));

        List<PostDto> result = postService.getDraftsByProject(testProjectId);

        assertSingleElementListWithTestPost(result);
    }

    @Test
    void getPublishedByUserMapsAllToDtos() {
        when(postRepository.findByAuthorIdAndPublishedTrueAndDeletedFalseOrderByPublishedAtDesc(testAuthorId))
                .thenReturn(List.of(post));

        List<PostDto> result = postService.getPublishedByUser(testAuthorId);

        assertSingleElementListWithTestPost(result);
    }

    @Test
    void getPublishedByProjectMapsAllToDtos() {
        when(postRepository.findByProjectIdAndPublishedTrueAndDeletedFalseOrderByPublishedAtDesc(testProjectId))
                .thenReturn(List.of(post));

        List<PostDto> result = postService.getPublishedByProject(testProjectId);

        assertSingleElementListWithTestPost(result);
    }
}