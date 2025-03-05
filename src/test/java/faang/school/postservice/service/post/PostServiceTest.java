package faang.school.postservice.service.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.json.student.DtoBanShema;
import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.kafka.producer.KafkaPostProducer;
import faang.school.postservice.publisher.MessageSenderForUserBanImpl;
import faang.school.postservice.dto.post.*;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.post.PostMapperImpl;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.album.AlbumService;
import faang.school.postservice.service.amazons3.Amazons3ServiceImpl;
import faang.school.postservice.service.amazons3.processing.KeyKeeper;
import faang.school.postservice.sheduler.postcorrector.ginger.GingerCorrector;
import faang.school.postservice.service.resource.ResourceServiceImpl;
import faang.school.postservice.validator.dto.project.ProjectDtoValidator;
import faang.school.postservice.validator.dto.user.UserDtoValidator;
import faang.school.postservice.validator.file.FileValidator;
import faang.school.postservice.validator.post.PostIdValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @InjectMocks
    private PostService postService;
    @Mock
    private PostMapperImpl postMapper;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserServiceClient userService;
    @Mock
    private ProjectServiceClient projectService;
    @Mock
    private AlbumService albumService;
    @Mock
    private ResourceServiceImpl resourceServiceImpl;
    @Mock
    private UserDtoValidator userDtoValidator;
    @Mock
    private ProjectDtoValidator projectDtoValidator;
    @Mock
    private PostIdValidator postIdValidator;
    @Mock
    private KeyKeeper keyKeeper;
    @Mock
    private GingerCorrector gingerCorrector;
    @Mock
    private MessageSenderForUserBanImpl messageSenderForUserBan;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private KafkaPostProducer kafkaPostProducer;
    @Mock
    private Amazons3ServiceImpl amazonS3;
    @Mock
    private FileValidator fileValidator;
    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @ParameterizedTest
    @MethodSource("validRequestsDraftDto")
    void testCreateDraftPost_withValidInputDto_shouldCreateAndReturnPostDraftResponseDto(PostDraftCreateDto requestDto) {

        if (requestDto.getAuthorId() != null) {
            when(userService.getUser(anyLong())).thenReturn(new UserDto());
            doNothing().when(userDtoValidator).validateUserDto(new UserDto());
        }
        if (requestDto.getProjectId() != null) {
            when(projectService.getProject(anyLong())).thenReturn(new ProjectDto());
            doNothing().when(projectDtoValidator).validateProjectDto(new ProjectDto());
        }
        when(postMapper.toEntityFromDraftDto(requestDto)).thenReturn(new Post());
        if (requestDto.getAlbumsId() != null) {
            when(albumService.getAlbumsByIds(any())).thenReturn(List.of(new Album(), new Album()));
        }
        if (requestDto.getResourcesId() != null) {
            when(resourceServiceImpl.getResourcesByIds(any())).thenReturn(List.of(new Resource(), new Resource()));
        }
        when(postRepository.save(any())).thenReturn(new Post());
        PostDraftResponseDto responseDto = mock(PostDraftResponseDto.class);
        when(postMapper.toDraftDtoFromPost(any(Post.class))).thenReturn(responseDto);

        PostDraftResponseDto result = postService.createDraftPost(requestDto);

        if (requestDto.getAuthorId() != null) {
            verify(userService, times(1)).getUser(anyLong());
            verify(userDtoValidator, times(1)).validateUserDto(new UserDto());
        }
        if (requestDto.getProjectId() != null) {
            verify(projectService, times(1)).getProject(anyLong());
            verify(projectDtoValidator, times(1)).validateProjectDto(new ProjectDto());
        }
        if (requestDto.getAlbumsId() != null) {
            verify(albumService, times(1)).getAlbumsByIds(any());
        }
        if (requestDto.getResourcesId() != null) {
            verify(resourceServiceImpl, times(1)).getResourcesByIds(any());
        }
        verify(postMapper, times(1)).toEntityFromDraftDto(requestDto);
        verify(postRepository, times(1)).save(any());
        verify(postMapper, times(1)).toDraftDtoFromPost(any(Post.class));

        Set<ConstraintViolation<PostDraftCreateDto>> violations = validator.validate(requestDto);
        assertTrue(violations.isEmpty());
        assertNotNull(result);
    }

    @Test
    void positiveFindPostById() {
        Long postId = 1L;

        Post post = Post.builder().id(1L).build();

        doNothing().when(postIdValidator).postIdValidate(postId);
        when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(post));

        Post result = postService.findPostById(postId);

        verify(postIdValidator, times(1)).postIdValidate(postId);
        verify(postRepository, times(1)).findById(postId);

        assertNotNull(result);
        assertEquals(postId, result.getId());
    }

    @ParameterizedTest
    @MethodSource("invalidRequestsDraftDto")
    void testCreateDraftPost_withInvalidInputDto_shouldThrowConstraintViolationException(PostDraftCreateDto dto) {
        Set<ConstraintViolation<PostDraftCreateDto>> violations = validator.validate(dto);
        verify(postRepository, times(0)).save(any());
        assertFalse(violations.isEmpty());
    }

    @Test
    void testCreateDraftPost_withNotExistsAuthor_shouldThrowEntityNotFoundException() {
        PostDraftCreateDto requestDto = PostDraftCreateDto.builder()
                .content("content")
                .authorId(1L)
                .build();

        when(userService.getUser(anyLong())).thenReturn(null);
        doThrow(new EntityNotFoundException("User not found"))
                .when(userDtoValidator)
                .validateUserDto(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> postService.createDraftPost(requestDto)
        );

        verify(userService, times(1)).getUser(anyLong());
        verify(postRepository, times(0)).save(any());

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testCreateDraftPost_withNotExistsProject_shouldThrowEntityNotFoundException() {
        PostDraftCreateDto requestDto = PostDraftCreateDto.builder()
                .content("content")
                .projectId(1L)
                .build();

        when(projectService.getProject(anyLong())).thenReturn(null);
        doThrow(new EntityNotFoundException("Project not found"))
                .when(projectDtoValidator)
                .validateProjectDto(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> postService.createDraftPost(requestDto)
        );

        verify(projectService, times(1)).getProject(anyLong());
        verify(postRepository, times(0)).save(any());

        assertEquals("Project not found", exception.getMessage());
    }


    @Test
    void testPublishPost_withValidPostId_shouldPublishAndReturnPostResponseDto() {
        long postId = 1L;
        PostResponseDto responseDto = PostResponseDto.builder()
                .id(1L)
                .content("content")
                .authorId(1L)
                .published(true)
                .build();

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(new Post()));
        when(postRepository.save(any())).thenReturn(new Post());
        when(postMapper.toDtoFromPost(any(Post.class))).thenReturn(responseDto);

        PostResponseDto result = postService.publishPost(postId);

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).save(any());
        verify(postMapper, times(1)).toDtoFromPost(any(Post.class));

        assertNotNull(result);
        assertEquals(result, responseDto);
    }

    @Test
    void testPublishPost_withNotExistsPost_shouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.publishPost(anyLong())
        );
        verify(postRepository, times(0)).save(any());
        assertTrue(exception.getMessage().contains("Post not found"));
    }

    @Test
    void testPublishPost_withPostAlreadyPublished_shouldThrowIllegalArgumentException() {
        long postId = 1L;
        Post post = Post.builder()
                .id(1L)
                .content("content")
                .authorId(1L)
                .published(true)
                .build();

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.publishPost(postId)
        );

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(0)).save(any());

        assertTrue(exception.getMessage().contains("Post is already published"));
    }

    @Test
    void negativeFindPostById() {
        Long postId = 1L;

        doNothing().when(postIdValidator).postIdValidate(postId);
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> postService.findPostById(postId));

        verify(postIdValidator, times(1)).postIdValidate(postId);
        verify(postRepository, times(1)).findById(postId);

        assertNotNull(exception);
        assertEquals("Comment not found", exception.getMessage());
    }


    @Test
    void testUpdatePost_withValidPostIdAndDto_shouldUpdateAndReturnPostResponseDto() {
        long postId = 1L;
        PostUpdateDto requestDto = PostUpdateDto.builder()
                .content("contentUpdate")
                .build();
        PostResponseDto responseDto = PostResponseDto.builder()
                .id(1L)
                .content("contentUpdate")
                .authorId(1L)
                .build();
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(new Post()));
        when(postRepository.save(any())).thenReturn(new Post());
        when(postMapper.toDtoFromPost(any(Post.class))).thenReturn(responseDto);

        PostResponseDto result = postService.updatePost(postId, requestDto);

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).save(any());
        verify(postMapper, times(1)).toDtoFromPost(any(Post.class));

        assertNotNull(result);
        assertEquals(result, responseDto);
    }

    @Test
    void positiveExistsPost() {
        Long postId = 1L;
        doNothing().when(postIdValidator).postIdValidate(postId);
        when(postRepository.existsById(postId)).thenReturn(true);

        postService.existsPost(postId);

        verify(postIdValidator, times(1)).postIdValidate(postId);
        verify(postRepository, times(1)).existsById(postId);
    }

    @Test
    void negativeExistsPost() {
        doThrow(new IllegalArgumentException("Invalid post ID")).when(postIdValidator).postIdValidate(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.existsPost(null));

        verify(postIdValidator, times(1)).postIdValidate(null);

        verify(postRepository, never()).existsById(null);

        Assertions.assertEquals("Invalid post ID", exception.getMessage());
    }

    @Test
    void testUpdatePost_withInValidRequestDto_shouldThrowConstraintViolationException() {
        PostUpdateDto requestDto = PostUpdateDto.builder().build();

        Set<ConstraintViolation<PostUpdateDto>> violations = validator.validate(requestDto);
        verify(postRepository, times(0)).save(any());
        assertFalse(violations.isEmpty());
    }

    @Test
    void testUpdatePost_withNotExistsPost_shouldThrowIllegalArgumentException() {
        PostUpdateDto requestDto = PostUpdateDto.builder().content("content").build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.updatePost(anyLong(), requestDto)
        );
        verify(postRepository, times(0)).save(any());
        assertTrue(exception.getMessage().contains("Post not found"));
    }

    @Test
    void testDeletePost_withValidPostId_shouldDeleteAndReturnPostResponseDto() {
        long postId = 1L;
        PostResponseDto responseDto = PostResponseDto.builder()
                .id(1L)
                .content("contentUpdate")
                .authorId(1L)
                .deleted(true)
                .build();

        when(postRepository.findById(anyLong())).thenReturn(Optional.of(new Post()));
        when(postRepository.save(any())).thenReturn(new Post());
        when(postMapper.toDtoFromPost(any(Post.class))).thenReturn(responseDto);

        PostResponseDto result = postService.deletePost(postId);

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).save(any());
        verify(postMapper, times(1)).toDtoFromPost(any(Post.class));

        assertNotNull(result);
        assertEquals(result, responseDto);
    }

    @Test
    void testDeletePost_withNotExistsPost_shouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.deletePost(anyLong())
        );
        verify(postRepository, times(0)).save(any());
        assertTrue(exception.getMessage().contains("Post not found"));
    }

    @Test
    void testGetPost_withValidPostId_returnPostResponseDto() {
        long postId = 1L;
        PostResponseDto responseDto = PostResponseDto.builder()
                .id(1L)
                .content("content")
                .authorId(1L)
                .build();
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(new Post()));
        when(postMapper.toDtoFromPost(any(Post.class))).thenReturn(responseDto);

        PostResponseDto result = postService.getPost(postId);

        verify(postRepository, times(1)).findById(postId);
        verify(postMapper, times(1)).toDtoFromPost(any(Post.class));

        assertNotNull(result);
        assertEquals(result, responseDto);
    }

    @Test
    void testGetPost_withNotExistsPost_shouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> postService.getPost(anyLong())
        );
        verify(postRepository, times(0)).save(any());
        assertTrue(exception.getMessage().contains("Post not found"));
    }

    @Test
    void testGetDraftPostsByUserIdSortedCreatedAtDesc_Positive() {
        long userId = 1L;
        List<Post> posts = new ArrayList<>(List.of(
                Post.builder()
                        .id(1L)
                        .authorId(userId)
                        .content("content1")
                        .deleted(false)
                        .published(false)
                        .build(),
                Post.builder()
                        .id(2L)
                        .authorId(userId)
                        .content("content2")
                        .deleted(false)
                        .published(false)
                        .build()
        ));
        List<PostDraftResponseDto> responseDtos = new ArrayList<>(List.of(
                PostDraftResponseDto.builder()
                        .id(1L)
                        .authorId(userId)
                        .content("content1")
                        .deleted(false)
                        .published(false)
                        .build(),
                PostDraftResponseDto.builder()
                        .id(2L)
                        .authorId(userId)
                        .content("content2")
                        .deleted(false)
                        .published(false)
                        .build()
        ));

        when(postRepository.findByNotPublishedAndNotDeletedAndAuthorIdOrderCreatedAtDesc(userId)).thenReturn(posts);
        when(postMapper.toDraftDtoFromPost(posts.get(0))).thenReturn(responseDtos.get(0));
        when(postMapper.toDraftDtoFromPost(posts.get(1))).thenReturn(responseDtos.get(1));

        List<PostDraftResponseDto> result = postService.getDraftPostsByUserIdSortedCreatedAtDesc(userId);

        verify(postRepository, times(1)).findByNotPublishedAndNotDeletedAndAuthorIdOrderCreatedAtDesc(userId);
        verify(postMapper, times(1)).toDraftDtoFromPost(posts.get(0));

        assertEquals(result.size(), responseDtos.size());
        assertEquals(result.get(0), responseDtos.get(0));
    }

    @Test
    void testGetDraftPostsByProjectIdSortedCreatedAtDesc_Positive() {
        long projectId = 1L;
        List<Post> posts = new ArrayList<>(List.of(
                Post.builder()
                        .id(1L)
                        .projectId(projectId)
                        .content("content1")
                        .deleted(false)
                        .published(false)
                        .build(),
                Post.builder()
                        .id(2L)
                        .projectId(projectId)
                        .content("content2")
                        .deleted(false)
                        .published(false)
                        .build()
        ));
        List<PostDraftResponseDto> responseDtos = new ArrayList<>(List.of(
                PostDraftResponseDto.builder()
                        .id(1L)
                        .projectId(projectId)
                        .content("content1")
                        .deleted(false)
                        .published(false)
                        .build(),
                PostDraftResponseDto.builder()
                        .id(2L)
                        .projectId(projectId)
                        .content("content2")
                        .deleted(false)
                        .published(false)
                        .build()
        ));

        when(postRepository.findByNotPublishedAndNotDeletedAndProjectIdOrderCreatedAtDesc(projectId)).thenReturn(posts);
        when(postMapper.toDraftDtoFromPost(posts.get(0))).thenReturn(responseDtos.get(0));
        when(postMapper.toDraftDtoFromPost(posts.get(1))).thenReturn(responseDtos.get(1));

        List<PostDraftResponseDto> result = postService.getDraftPostsByProjectIdSortedCreatedAtDesc(projectId);

        verify(postRepository, times(1)).findByNotPublishedAndNotDeletedAndProjectIdOrderCreatedAtDesc(projectId);
        verify(postMapper, times(1)).toDraftDtoFromPost(posts.get(0));

        assertEquals(result.size(), responseDtos.size());
        assertEquals(result.get(0), responseDtos.get(0));
    }


    @Test
    void testGetPublishPostsByUserIdSortedCreatedAtDesc_Positive() {
        long userId = 1L;
        List<Post> posts = new ArrayList<>(List.of(
                Post.builder()
                        .id(1L)
                        .authorId(userId)
                        .content("content1")
                        .deleted(false)
                        .published(true)
                        .build(),
                Post.builder()
                        .id(2L)
                        .authorId(userId)
                        .content("content2")
                        .published(true)
                        .deleted(false)
                        .build()
        ));
        List<PostResponseDto> responseDtos = new ArrayList<>(List.of(
                PostResponseDto.builder()
                        .id(1L)
                        .authorId(userId)
                        .content("content1")
                        .published(true)
                        .deleted(false)
                        .build(),
                PostResponseDto.builder()
                        .id(2L)
                        .authorId(userId)
                        .content("content2")
                        .published(true)
                        .deleted(false)
                        .build()
        ));

        when(postRepository.findByPublishedAndNotDeletedAndAuthorIdOrderCreatedAtDesc(userId)).thenReturn(posts);
        when(postMapper.toDtoFromPost(posts.get(0))).thenReturn(responseDtos.get(0));
        when(postMapper.toDtoFromPost(posts.get(1))).thenReturn(responseDtos.get(1));

        List<PostResponseDto> result = postService.getPublishPostsByUserIdSortedCreatedAtDesc(userId);

        verify(postRepository, times(1)).findByPublishedAndNotDeletedAndAuthorIdOrderCreatedAtDesc(userId);
        verify(postMapper, times(1)).toDtoFromPost(posts.get(0));

        assertEquals(result.size(), responseDtos.size());
        assertEquals(result.get(0), responseDtos.get(0));
    }

    @Test
    void testGetPublishPostsByProjectIdSortedCreatedAtDesc_Positive() {
        long projectId = 1L;
        List<Post> posts = new ArrayList<>(List.of(
                Post.builder()
                        .id(1L)
                        .projectId(projectId)
                        .content("content1")
                        .published(true)
                        .deleted(false)
                        .build(),
                Post.builder()
                        .id(2L)
                        .projectId(projectId)
                        .content("content2")
                        .published(true)
                        .deleted(false)
                        .build()
        ));
        List<PostResponseDto> responseDtos = new ArrayList<>(List.of(
                PostResponseDto.builder()
                        .id(1L)
                        .projectId(projectId)
                        .content("content1")
                        .published(true)
                        .deleted(false)
                        .build(),
                PostResponseDto.builder()
                        .id(2L)
                        .projectId(projectId)
                        .content("content2")
                        .published(true)
                        .deleted(false)
                        .build()
        ));

        when(postRepository.findByPublishedAndNotDeletedAndProjectIdOrderCreatedAtDesc(projectId)).thenReturn(posts);
        when(postMapper.toDtoFromPost(posts.get(0))).thenReturn(responseDtos.get(0));
        when(postMapper.toDtoFromPost(posts.get(1))).thenReturn(responseDtos.get(1));

        List<PostResponseDto> result = postService.getPublishPostsByProjectIdSortedCreatedAtDesc(projectId);

        verify(postRepository, times(1)).findByPublishedAndNotDeletedAndProjectIdOrderCreatedAtDesc(projectId);
        verify(postMapper, times(1)).toDtoFromPost(posts.get(0));

        assertEquals(result.size(), responseDtos.size());
        assertEquals(result.get(0), responseDtos.get(0));
    }

    static Stream<Object[]> validRequestsDraftDto() {
        return Stream.of(
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .authorId(1L)
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .projectId(1L)
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .authorId(1L)
                        .albumsId(new ArrayList<>(List.of(1L, 2L)))
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .authorId(1L)
                        .resourcesId(new ArrayList<>(List.of(1L, 2L)))
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .authorId(1L)
                        .albumsId(new ArrayList<>(List.of(1L, 2L)))
                        .resourcesId(new ArrayList<>(List.of(1L, 2L)))
                        .build()}
        );
    }

    static Stream<Object[]> invalidRequestsDraftDto() {
        return Stream.of(
                new Object[]{PostDraftCreateDto.builder()
                        .content("    ")
                        .authorId(1L)
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .authorId(1L)
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .authorId(1L)
                        .projectId(1L)
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .authorId(-1L)
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .projectId(-2L)
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .authorId(-2L)
                        .projectId(-1L)
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .authorId(0L)
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .projectId(0L)
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .authorId(0L)
                        .projectId(0L)
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .projectId(1L)
                        .resourcesId(new ArrayList<>(List.of(1L, -2L)))
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .projectId(1L)
                        .albumsId(new ArrayList<>(List.of(1L, -2L)))
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .projectId(1L)
                        .albumsId(new ArrayList<>(List.of()))
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .projectId(1L)
                        .resourcesId(new ArrayList<>(List.of()))
                        .build()},
                new Object[]{PostDraftCreateDto.builder()
                        .content("content")
                        .projectId(1L)
                        .albumsId(new ArrayList<>(List.of()))
                        .resourcesId(new ArrayList<>(List.of()))
                        .build()}
        );
    }

    @Test
    @DisplayName("Test method createDraftPostWithFiles")
    void testMethodCreateDraftPostWithFiles() throws IOException {
        PostDraftWithFilesCreateDto createDto = PostDraftWithFilesCreateDto.builder()
                .content("content")
                .authorId(1L)
                .projectId(1L)
                .albumsId(List.of(1L))
                .build();
        PostDraftResponseDto responseDto = PostDraftResponseDto.builder()
                .id(1L)
                .content("content")
                .albumsIds(List.of(1L))
                .resourcesIds(List.of(1L, 2L))
                .authorId(1L)
                .projectId(1L)
                .build();
        UserDto userDto = UserDto.builder().id(1L).build();
        MockMultipartFile[] files = new MockMultipartFile[]{createMultipartFile("Alone")};
        Post post = Post.builder()
                .id(1L)
                .content("content")
                .authorId(1L)
                .projectId(1L)
                .albums(List.of(Album.builder().id(1L).build()))
                .resources(new ArrayList<>())
                .build();
        Resource resource = Resource.builder()
                .id(2L)
                .key("1:files:1")
                .type("image/png")
                .build();
        Resource resource2 = Resource.builder()
                .key("1:files:1")
                .name("Alone")
                .type("image/png")
                .size(5L)
                .build();

        when(userService.getUser(1L)).thenReturn(userDto);
        when(projectService.getProject(1L)).thenReturn(ProjectDto.builder().id(1L).build());
        when(postMapper.toEntityFromDraftDtoWithFiles(createDto)).thenReturn(post);
        when(albumService.getAlbumsByIds(List.of(1L))).thenReturn(List.of(Album.builder().id(1L).build()));
        when(keyKeeper.getKeyFile("1:files:1")).thenReturn("1:files:1");
        when(resourceServiceImpl.save(resource2)).thenReturn(resource);
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.toDraftDtoFromPost(post)).thenReturn(responseDto);
        when(postService.createDraftPostWithFiles(createDto, files)).thenReturn(responseDto);

        PostDraftResponseDto expectedDto = postService.createDraftPostWithFiles(createDto, files);
        assertEquals(expectedDto, responseDto);
    }

    @Test
    @DisplayName("Test positive updatePostWithFiles")
    void testUpdatePostWithFiles() throws IOException {
        MockMultipartFile[] files = new MockMultipartFile[]{createMultipartFile("Alone")};
        PostUpdateDto updateDto = PostUpdateDto.builder()
                .content("new content")
                .resourcesIds(List.of(1L))
                .build();
        PostResponseDto responseDto = PostResponseDto.builder()
                .id(1L)
                .content("new content")
                .albumsIds(List.of(1L))
                .resourcesIds(List.of(1L, 2L))
                .authorId(1L)
                .projectId(1L)
                .build();
        long postId = 1L;
        Post post = Post.builder()
                .id(1L)
                .content("content")
                .authorId(1L)
                .projectId(1L)
                .albums(List.of(Album.builder().id(1L).build()))
                .resources(List.of(Resource.builder().id(1L).build()))
                .build();
        List<Resource> resources = new ArrayList<>();
        resources.add(Resource.builder().id(1L).build());

        Resource resource = Resource.builder()
                .id(2L)
                .key("1:files:1")
                .type("image/png")
                .build();
        Resource resource2 = Resource.builder()
                .key("1:files:1")
                .name("Alone")
                .type("image/png")
                .size(5L)
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(resourceServiceImpl.getResourcesByIds(List.of(1L))).thenReturn(resources);
        when(keyKeeper.getKeyFile("1:files:1")).thenReturn("1:files:1");
        when(resourceServiceImpl.save(resource2)).thenReturn(resource);
        when(postRepository.save(post)).thenReturn(post);
        when(postMapper.toDtoFromPost(post)).thenReturn(responseDto);
        PostResponseDto expectedDto = postService.updatePostWithFiles(postId, updateDto, files);

        assertEquals(expectedDto, responseDto);
    }

    @Test
    void testPublishScheduledPosts_Positive() throws Exception {
        int partitionSize = 1;
        List<Post> posts = Arrays.asList(
                Post.builder().id(1L).content("content1").published(false).publishedAt(null).deleted(false).build(),
                Post.builder().id(2L).content("content2").published(false).publishedAt(null).deleted(false).build()
        );

        when(postRepository.findReadyToPublish()).thenReturn(posts);

        postService.publishScheduledPosts(partitionSize);
        Thread.sleep(1000);

        verify(postRepository, atLeastOnce()).saveAll(anyList());
        assertTrue(posts.stream().allMatch(Post::isPublished));
        assertTrue(posts.stream().allMatch(post -> post.getPublishedAt() != null));
    }

    @Test
    void testPublishScheduledPosts_noPosts() {
        int partitionSize = 1;
        when(postRepository.findReadyToPublish()).thenReturn(Collections.emptyList());
        postService.publishScheduledPosts(partitionSize);
        verify(postRepository, never()).saveAll(anyList());
    }

    @Test
    void asyncPublishPosts() throws Exception {
        List<Post> posts = Arrays.asList(
                Post.builder().id(1L).content("content1").published(false).publishedAt(null).deleted(false).build(),
                Post.builder().id(2L).content("content2").published(false).publishedAt(null).deleted(false).build()
        );

        postService.asyncPublishPosts(posts);
        Thread.sleep(1000);

        verify(postRepository).saveAll(posts);
        assertTrue(posts.stream().allMatch(Post::isPublished));
    }

    private MockMultipartFile createMultipartFile(String fileName) {
        return new MockMultipartFile("file",
                fileName,
                "image/png",
                "Hello".getBytes());
    }

    @Test
    @DisplayName("Test method testCheckingPostForErrors")
    void testCheckingPostForErrors() throws IOException, InterruptedException {
        Post post = Post.builder().id(1L).build();
        List<Post> posts = List.of(
                Post.builder().id(1L).content("HHeello").build(),
                Post.builder().id(2L).content("HHeello").build(),
                Post.builder().id(3L).content("HHeello").build(),
                Post.builder().id(4L).content("HHeello").build(),
                Post.builder().id(5L).content("HHeello").build(),
                Post.builder().id(6L).content("HHeello").build(),
                Post.builder().id(7L).content("HHeello").build(),
                Post.builder().id(8L).content("HHeello").build(),
                Post.builder().id(9L).content("HHeello").build(),
                Post.builder().id(10L).content("HHeello").build(),
                Post.builder().id(11L).content("HHeello").build(),
                Post.builder().id(12L).content("HHeello").build(),
                Post.builder().id(13L).content("HHeello").build(),
                Post.builder().id(14L).content("HHeello").build(),
                Post.builder().id(15L).content("HHeello").build()
        );

        when(postRepository.findByNotPublished()).thenReturn(posts);
        when(gingerCorrector.correct(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        postService.checkingPostForErrors();

        verify(postRepository, times(1)).findByNotPublished();
        verify(postRepository, times(3)).saveAll(anyList());
    }

    @Test
    @DisplayName("Test positive method checkPostsForVerification")
    void testPositiveCheckPostsForVerification() throws IOException {
        List<Post> posts = List.of(
                Post.builder().id(1L).authorId(1L).verified(false).build(),
                Post.builder().id(7L).authorId(1L).verified(false).build(),
                Post.builder().id(8L).authorId(1L).verified(false).build(),
                Post.builder().id(7L).authorId(1L).verified(false).build(),
                Post.builder().id(25L).authorId(2L).verified(false).build(),
                Post.builder().id(8L).authorId(2L).verified(false).build(),
                Post.builder().id(98L).authorId(2L).verified(false).build(),
                Post.builder().id(567L).authorId(2L).verified(false).build(),
                Post.builder().id(245L).authorId(2L).verified(false).build()
        );
        List<Long> userIds = List.of(1L, 2L);
        DtoBanShema dtoBanShema = new DtoBanShema();
        dtoBanShema.setIds(userIds);
        String prefix = "[1,2]";

        when(postRepository.findByNotVerified()).thenReturn(posts);
        when(objectMapper.writeValueAsString(dtoBanShema)).thenReturn(prefix);
        postService.checkPostsForVerification();
        verify(messageSenderForUserBan, times((1))).send(objectMapper.writeValueAsString(dtoBanShema));
    }

    @Test
    @DisplayName("Test negative method checkPostsForVerification")
    void testNegativeCheckPostsForVerification() throws IOException {
        List<Post> posts = List.of(
                Post.builder().id(1L).authorId(1L).verified(false).build(),
                Post.builder().id(7L).authorId(1L).verified(false).build(),
                Post.builder().id(25L).authorId(2L).verified(false).build(),
                Post.builder().id(8L).authorId(2L).verified(false).build(),
                Post.builder().id(10L).authorId(2L).verified(false).build()
        );
        List<Long> userIds = List.of(1L, 2L);
        DtoBanShema dtoBanShema = new DtoBanShema();
        dtoBanShema.setIds(userIds);
        postService.setSizeNotVerifiedPostsForUsers(5);

        when(postRepository.findByNotVerified()).thenReturn(posts);
        postService.checkPostsForVerification();
        verify(messageSenderForUserBan, times((0))).send(objectMapper.writeValueAsString(dtoBanShema));
    }
}