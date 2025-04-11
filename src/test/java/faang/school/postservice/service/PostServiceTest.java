package faang.school.postservice.service;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.exception.PostValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    private static final long EXISTENT_POST_ID = 1L;
    private static final long NON_EXISTENT_POST_ID = 3L;
    private static final long EXISTENT_AUTHOR_ID = 1L;
    private static final long NON_EXISTENT_AUTHOR_ID = 3L;
    private static final long EXISTENT_PROJECT_ID = 1L;
    private static final long NON_EXISTENT_PROJECT_ID = 3L;
    private static final LocalDateTime FIXED_TIME = LocalDateTime.of(2020, 4, 1, 12, 0, 0);

    private PostDto draftDto;
    private Post draft;
    private Post post;
    private PostDto postDto;


    private Post draft1;
    private Post draft2;
    private Post draft3;
    private Post draft4;
    private PostDto draftDto1;
    private PostDto draftDto2;
    private PostDto draftDto3;

    private Post post1;
    private Post post2;
    private Post post3;
    private PostDto postDto1;
    private PostDto postDto2;
    private PostDto postDto3;


    private Post postWithCreatedAt;
    private PostDto postDtoWithCreatedAt;
    private Post publishedPost;
    private PostDto publishedpostDto;

    private List<Post> draftListWithAuthorId1;
    private List<PostDto> draftDtoList;
    private List<Post> postList;
    private List<PostDto> postDtoList;


    private List<Post> filteredDraftList;
    private List<PostDto> filteredDraftDtoList;
    private List<Post> filteredPostList;
    private List<PostDto> filteredPostDtoList;

    @Captor
    private ArgumentCaptor<Post> postCaptor;
    @Captor
    private ArgumentCaptor<List<Post>> postListCaptor;
    @Mock
    private Clock fixedClock;
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostValidator postValidator;
    @Mock
    private PostMapper postMapper;
    @InjectMocks
    private PostServiceImpl postService;

    @BeforeEach
    void setUp() {
        draftDto = PostDto.builder()
                .content("Test Content")
                .authorId(1L)
                .published(false)
                .createdAt(FIXED_TIME)
                .build();
        draft = Post.builder()
                .content("Test Content")
                .authorId(1L)
                .published(false)
                .createdAt(FIXED_TIME)
                .build();

        draftDto1 = PostDto.builder()
                .content("Test Content")
                .authorId(1L)
                .published(false)
                .createdAt(FIXED_TIME)
                .build();
        draft1 = Post.builder()
                .content("Test Content")
                .authorId(1L)
                .published(false)
                .deleted(false)
                .createdAt(FIXED_TIME)
                .build();
        draftDto2 = PostDto.builder()
                .content("Test Content2")
                .authorId(2L)
                .published(false)
                .createdAt(LocalDateTime.of(2024, 6, 1, 12, 0, 0))
                .build();
        draft2 = Post.builder()
                .content("Test Content2")
                .authorId(2L)
                .published(false)
                .deleted(false)
                .createdAt(LocalDateTime.of(2024, 6, 1, 12, 0, 0))
                .build();
        draftDto3 = PostDto.builder()
                .content("Test Content3")
                .authorId(1L)
                .published(false)
                .createdAt(LocalDateTime.of(2023, 10, 1, 12, 0, 0))
                .build();
        draft3 = Post.builder()
                .content("Test Content3")
                .authorId(1L)
                .published(false)
                .deleted(false)
                .createdAt(LocalDateTime.of(2023, 10, 1, 12, 0, 0))
                .build();
        draft4 = Post.builder()
                .content("Test Content4")
                .authorId(1L)
                .published(false)
                .deleted(true)
                .createdAt(LocalDateTime.of(2023, 10, 1, 12, 0, 0))
                .build();

        draftListWithAuthorId1 = List.of(draft1, draft3, draft4);
        draftDtoList = List.of(draftDto, draftDto2, draftDto3);
        filteredDraftList = List.of(draft3, draft1);
        filteredDraftDtoList = List.of(draftDto3, draftDto);

        postDto = PostDto.builder()
                .content("Test Content")
                .authorId(1L)
                .published(false)
                .createdAt(FIXED_TIME)
                .build();
        post = Post.builder()
                .content("Test Content")
                .authorId(1L)
                .published(false)
                .createdAt(FIXED_TIME)
                .build();
        postDto1 = PostDto.builder()
                .content("Test Content")
                .authorId(1L)
                .published(false)
                .createdAt(FIXED_TIME)
                .build();
        post1 = Post.builder()
                .content("Test Content")
                .authorId(1L)
                .published(false)
                .createdAt(FIXED_TIME)
                .build();
        postDto1 = PostDto.builder()
                .content("Test Content")
                .authorId(1L)
                .published(false)
                .createdAt(FIXED_TIME)
                .build();
        post1 = Post.builder()
                .content("Test Content")
                .authorId(1L)
                .published(false)
                .createdAt(FIXED_TIME)
                .build();
        postDto2 = PostDto.builder()
                .content("Test Content2")
                .authorId(2L)
                .published(false)
                .createdAt(LocalDateTime.of(2024, 6, 1, 12, 0, 0))
                .build();
        post2 = Post.builder()
                .content("Test Content2")
                .authorId(2L)
                .published(false)
                .createdAt(LocalDateTime.of(2024, 6, 1, 12, 0, 0))
                .build();
        postDto3 = PostDto.builder()
                .content("Test Content3")
                .authorId(1L)
                .published(false)
                .createdAt(LocalDateTime.of(2023, 10, 1, 12, 0, 0))
                .build();
        post3 = Post.builder()
                .content("Test Content3")
                .authorId(1L)
                .published(false)
                .createdAt(LocalDateTime.of(2023, 10, 1, 12, 0, 0))
                .build();
        postList = List.of(post1, post2, post3);
        postDtoList = List.of(postDto1, postDto2, postDto3);
        filteredPostList = List.of(post3, post1);
        filteredPostDtoList = List.of(postDto3, postDto1);

        publishedpostDto = PostDto.builder()
                .id(1L)
                .content("Test Content")
                .authorId(1L)
                .published(true)
                .createdAt(FIXED_TIME)
                .build();
        publishedPost = Post.builder()
                .id(1L)
                .content("Test Content")
                .authorId(1L)
                .published(true)
                .createdAt(FIXED_TIME)
                .build();

        postDtoWithCreatedAt = PostDto.builder()
                .content("Test Content")
                .authorId(1L)
                .published(false)
                .createdAt(FIXED_TIME)
                .build();
        postWithCreatedAt = Post.builder()
                .content("Test Content")
                .authorId(1L)
                .createdAt(FIXED_TIME)
                .published(false)
                .build();
    }


    @Test
    void testCreateDraft() {
        //Arrange
        when(fixedClock.instant()).thenReturn(FIXED_TIME.atZone(ZoneId.systemDefault()).toInstant());
        when(fixedClock.getZone()).thenReturn(ZoneId.systemDefault());
        doNothing().when(postValidator).validatePostDto(draftDto);
        when(postMapper.toEntity(draftDto)).thenReturn(draft);
        when(postRepository.save(draft)).thenReturn(draft);
        when(postMapper.toDto(draft)).thenReturn(postDtoWithCreatedAt);

        //Act
        PostDto createdDraft = postService.createDraft(draftDto);

        //Assert
        verify(postRepository).save(postCaptor.capture());
        assertEquals(postWithCreatedAt, postCaptor.getValue());

        assertEquals(draftDto.content(), createdDraft.content());
        assertEquals(draftDto.id(), createdDraft.id());
        assertNull(createdDraft.projectId());
        assertFalse(createdDraft.published());
        assertEquals(postDtoWithCreatedAt.createdAt(), createdDraft.createdAt());
    }

    @Test
    void testGetPostShouldThrowExceptionWhenPostDoesNotExist() {
        when(postRepository.findById(NON_EXISTENT_POST_ID)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.getPost(NON_EXISTENT_POST_ID));
    }

    @Test
    void testGetPostShouldReturnPostDtoWhenPostExists() {
        when(postRepository.findById(EXISTENT_POST_ID)).thenReturn(Optional.of(draft));
        when(postMapper.toDto(draft)).thenReturn(draftDto);

        PostDto actualPostDto = postService.getPost(EXISTENT_POST_ID);

        assertEquals(draftDto, actualPostDto);
    }

    @Test
    void testGetPostShouldThrowExceptionWhenPostAlreadyDeleted() {
        String errorMessage = "Post with ID = %d is deleted".formatted(EXISTENT_POST_ID);
       Post deletedPost = Post.builder()
               .id(EXISTENT_POST_ID) // Post exist in DB but marked as deleted
               .content("Test Content")
               .authorId(EXISTENT_AUTHOR_ID)
               .deleted(true)
               .build();
       when(postRepository.findById(EXISTENT_POST_ID)).thenReturn(Optional.of(deletedPost));
       PostNotFoundException exception = assertThrows(PostNotFoundException.class, () ->
               postService.getPost(EXISTENT_POST_ID));
       assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void testPublishPostShouldThrowExceptionWhenPostDoesNotExist() {
        when(postRepository.findById(NON_EXISTENT_POST_ID)).thenReturn(Optional.empty());

        PostNotFoundException exception = assertThrows(PostNotFoundException.class, () ->
                postService.publishPost(NON_EXISTENT_POST_ID));
        assertEquals("Post with id %d does not exist".formatted(NON_EXISTENT_POST_ID), exception.getMessage());
    }

    @Test
    void testPublishPostWhenPostIsUnpublished() {
        draft.setId(EXISTENT_POST_ID);
        when(postRepository.findById(EXISTENT_POST_ID)).thenReturn(Optional.of(draft));
        when(postRepository.save(any())).thenReturn(publishedPost);
        when(postMapper.toDto(publishedPost)).thenReturn(publishedpostDto);

        PostDto actualPostDto = postService.publishPost(EXISTENT_POST_ID);

        verify(postRepository).save(postCaptor.capture());
        assertTrue(postCaptor.getValue().isPublished());
        assertEquals(publishedpostDto, actualPostDto);
    }

    @Test
    void testPublishPostWhenPostIsAlreadyPublished() {
        when(postRepository.findById(EXISTENT_POST_ID)).thenReturn(Optional.of(publishedPost));

        PostValidationException exception = assertThrows(PostValidationException.class, () ->
                postService.publishPost(EXISTENT_POST_ID));
        assertEquals("Post is already published", exception.getMessage());
    }

    @Test
    void testUpdatePostShouldThrowExceptionWhenPostDoesNotExist() {
        String updatedContent = "Updated content";
        when(postRepository.findById(NON_EXISTENT_POST_ID)).thenReturn(Optional.empty());

        PostNotFoundException exception = assertThrows(PostNotFoundException.class, () ->
                postService.updatePost(NON_EXISTENT_POST_ID, updatedContent));
        assertEquals("Post with id %d does not exist".formatted(NON_EXISTENT_POST_ID),
                exception.getMessage());
    }

    @Test
    void testUpdatePostShouldUpdateContentWhenPostExists() {
        //Arrange
        String updatedContent = "Updated content";
        PostDto updatedPostDto = PostDto.builder()
                .id(1L)
                .content("Updated content")
                .authorId(1L)
                .published(true)
                .createdAt(FIXED_TIME)
                .updatedAt(FIXED_TIME)
                .build();
        Post updatedPost = Post.builder()
                .id(1L)
                .content("Updated content")
                .authorId(1L)
                .published(true)
                .createdAt(FIXED_TIME)
                .updatedAt(FIXED_TIME)
                .build();
        when(fixedClock.instant()).thenReturn(FIXED_TIME.atZone(ZoneId.systemDefault()).toInstant());
        when(fixedClock.getZone()).thenReturn(ZoneId.systemDefault());
        when(postRepository.findById(EXISTENT_POST_ID)).thenReturn(Optional.of(publishedPost));
        when(postRepository.save(any())).thenReturn(updatedPost);
        when(postMapper.toDto(updatedPost)).thenReturn(updatedPostDto);

        //Act
        PostDto actualUpdatedPostDto = postService.updatePost(EXISTENT_POST_ID, updatedContent);

        //Assert
        verify(postRepository).save(postCaptor.capture());
        assertEquals(updatedPost, postCaptor.getValue());
        assertEquals(updatedPostDto, actualUpdatedPostDto);
    }


    @Test
    void testSoftDeletePostWhenPostDoesNotExist() {
        when(postRepository.findById(NON_EXISTENT_POST_ID)).thenReturn(Optional.empty());
        PostNotFoundException exception = assertThrows(PostNotFoundException.class, () ->
                postService.softDeletePost(NON_EXISTENT_POST_ID));

        assertEquals("Post with id %d does not exist".formatted(NON_EXISTENT_POST_ID), exception.getMessage());
    }

    @Test
    void testSoftDeletePostWhenPostExists() {
        when(postRepository.findById(EXISTENT_POST_ID)).thenReturn(Optional.of(publishedPost));

        postService.softDeletePost(EXISTENT_POST_ID);

        verify(postRepository).save(postCaptor.capture());
        assertTrue(postCaptor.getValue().isDeleted());
    }

    @Test
    void testGetAllDraftsByAuthorIdShouldReturnsDraftsByAuthorIdWhenAuthorHasDrafts() {
        when(postRepository.findByAuthorId(EXISTENT_AUTHOR_ID)).thenReturn(draftListWithAuthorId1);

        List<PostDto> actualDtaftDtoList = postService.getAllDraftsByAuthorId(EXISTENT_AUTHOR_ID);
        verify(postMapper).toDtoList(postListCaptor.capture());
        assertEquals(filteredPostList, postListCaptor.getValue());
    }

    @Test
    void testGetAllDraftsByAuthorIdShouldReturnsEmptyListWhenAuthorDoesNotHaveDrafts() {
        when(postRepository.findByAuthorId(NON_EXISTENT_AUTHOR_ID)).thenReturn(List.of());

        List<PostDto> actualDtaftDtoList = postService.getAllDraftsByAuthorId(NON_EXISTENT_AUTHOR_ID);
        verify(postMapper).toDtoList(postListCaptor.capture());
        assertEquals(List.of(), postListCaptor.getValue());
    }

    public void testFindPostByIdWithThrow() {
        long postId = 1L;
        when(postRepository.findById(postId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(DataValidationException.class,
                () -> postService.findPostById(postId));
        assertEquals("Post with id 1 not found", exception.getMessage());
        verify(postRepository,times(1)).findById(postId);
    }

    @Test
    public void testFindPostById() {
        long postId = 1L;
        Post post = Post.builder().id(postId).build();
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        Post postResult = postService.findPostById(postId);
        verify(postRepository, times(1)).findById(postId);
        assertEquals(postId, postResult.getId());
    }
}